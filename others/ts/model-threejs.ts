import * as THREE from 'three';
import type { GLTF } from 'three/examples/jsm/loaders/GLTFLoader';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { PLYLoader } from 'three/examples/jsm/loaders/PLYLoader';
import { DropInViewer } from '@mkkellogg/gaussian-splats-3d';
import { RGBELoader } from 'three/examples/jsm/Addons.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { TransformControls } from 'three/examples/jsm/controls/TransformControls.js'
import { reactive, ref } from 'vue';


interface CameraCtrl {
  isMove: boolean;
  isRotate: boolean;
}

interface ObjCtrl {
  focusObj: any;
  modeType: 'translate' | 'rotate' | 'scale'
}


export default function useModelThree(width: number, height: number) {
  let cameraRef: any = ref();
  let sceneRef: any = ref();
  let rendererRef: any = ref();
  let camera: any = null;
  let scene: any = null;
  let renderer: any = null;
  let transformControl: any = null;
  let gsView: any = null;
  let objView: any[] = [];
  const progressState = ref<boolean>(false);
  const progressPercent = ref<number>(0);
  let _cameraLookAt = new THREE.Vector3(0, 0, 0)
  let cameraCtrlRef: CameraCtrl = reactive<CameraCtrl>({
    isMove: false,
    isRotate: false
  });
  let objCtrlRef: ObjCtrl = reactive<ObjCtrl>({
    focusObj: null,
    modeType: 'translate'
  })
  let canvasWidth = width, canvasHeight = height;

  initModelThreeVars();

  function addCube() {
    //创建一个长300，宽300，高0.01的长方体，将其旋转90度作为地面
    const floorMat = new THREE.MeshStandardMaterial({
      color: 0xa9a9a9, // 材质的颜色
    });
    const floorGeometry = new THREE.CylinderGeometry(10, 10, 1);
    const floorMesh = new THREE.Mesh(floorGeometry, floorMat);
    floorMesh.position.y = -0.5;
    floorMesh.receiveShadow = true;
    // floorMesh.rotation.x = -Math.PI / 2.0;

    scene.add(floorMesh);

    objView.push(floorMesh)
  }

  function resize(_width: number, _height: number) {
    canvasWidth = _width, canvasHeight = _height;
    renderer.setSize(canvasWidth, canvasHeight);
  }

  function initModelThreeVars() {

    // 相机配置
    camera = new THREE.PerspectiveCamera(75, canvasWidth / canvasHeight, 1, 100);
    camera.position.set(3, 3, 3);
    camera.lookAt(0, 0, 0);

    // 创建材质子菜单
    // const cameraFolder = gui.addFolder('camera');
    // const cameraPositionFolder = cameraFolder.addFolder('position');
    // cameraPositionFolder.add(guiObj.camera.position, 'x', 0, 10).name("x").onchange(() => {
    //     camera.position.set(guiObj.camera.position.x, guiObj.camera.position.y, guiObj.camera.position.z)
    // })
    // cameraPositionFolder.add(guiObj.camera.position, 'y', 0, 10).name("y").onchange(() => {
    //     camera.position.set(guiObj.camera.position.x, guiObj.camera.position.y, guiObj.camera.position.z)
    // })
    // cameraPositionFolder.add(guiObj.camera.position, 'z', 0, 10).name("z").onchange(() => {
    //     camera.position.set(guiObj.camera.position.x, guiObj.camera.position.y, guiObj.camera.position.z)
    // })

    // 渲染配置
    renderer = new THREE.WebGLRenderer({
      antialias: true, // 抗锯齿
      alpha: true // 用于设置透明度
    });
    renderer.setSize(width, height);

    // 设置渲染器，允许光源阴影渲染
    renderer.shadowMap.enabled = true;

    // 模型表面产生条纹影响渲染效果，可以改变.shadowMap.type默认值优化
    // renderer.shadowMap.type = THREE.VSMShadowMap;

    // 新版本，加载gltf，不需要执行下面代码解决颜色偏差
    // renderer.outputColorSpace = THREE.SRGBColorSpace;//设置为SRGB颜色空间

    // 设置背景颜色
    renderer.setClearColor(0x000000, 0);

    // 场景初始化
    scene = new THREE.Scene();
    scene.add(new THREE.AmbientLight(0x666666)); // 环境光

    // 添加控制器
    const controls = new OrbitControls(camera, renderer.domElement);
    transformControl = new TransformControls(camera, renderer.domElement)
    transformControl.addEventListener('change', () => {
      // console.log('模型拖动')
    })
    transformControl.addEventListener('dragging-changed', function (event: any) {
      controls.enabled = !event.value
    })

    transformControl.addEventListener('objectChange', (param: any) => {
      // console.log('模型拖动2')

    })
    scene.add(transformControl)


    // 平行光
    const directionalLight = new THREE.DirectionalLight(0xffffff, 3);
    // 平行光设置产生阴影的光源对象,开启光源阴影的计算功能
    directionalLight.castShadow = true;
    // 设置光源的方向：通过光源position属性和目标指向对象的position属性计算
    directionalLight.position.set(80, 100, 50);
    // directionalLight.shadow.camera.left = 80
    // directionalLight.shadow.camera.right = 50
    // directionalLight.shadow.camera.top = 100
    scene.add(directionalLight);

    // 可视化平行光阴影对应的正投影相机对象
    // const cameraHelper = new THREE.DirectionalLightHelper(directionalLight, 5, 0xff0000);
    // scene.add(cameraHelper);

    // // 可视化平行光阴影对应的正投影相机对象
    // const cameraHelper = new THREE.CameraHelper(directionalLight.shadow.camera);
    // scene.add(cameraHelper);

    // 聚光灯,参数2即光照强度
    // spotLight = new THREE.SpotLight(0xffffff, 1.0);
    // spotLight.intensity = 5;//光照强度
    // spotLight.decay = 1.0;//衰弱，0不衰弱

    // // 发散角度
    // spotLight.angle = Math.PI / 12;
    // spotLight.castShadow = true;
    // // 设置聚光光源位置
    // spotLight.position.set(80, 100, 50);
    // // 指向目标
    // spotLight.target.position.set(0, 0, 0);
    // scene.add(spotLight);//光源添加到场景中

    // const spotLightHelper = new THREE.SpotLightHelper(spotLight, 0xffffff)
    // scene.add(spotLightHelper);

    // const ambient = new THREE.AmbientLight(0xffffff, 0.4);
    // scene.add(ambient);

    // gui.add(ambient, 'intensity', 0, 2).name('环境光.intensity');

    cameraRef.value = camera
    sceneRef.value = scene
    rendererRef.value = renderer

    addCube();

    renderer.domElement.addEventListener('dblclick', function (event: any) {
      // .offsetY、.offsetX以canvas画布左上角为坐标原点,单位px
      const px = event.offsetX;
      const py = event.offsetY;
      //屏幕坐标px、py转WebGL标准设备坐标x、y
      //width、height表示canvas画布宽高度
      const x = (px / canvasWidth) * 2 - 1;
      const y = -(py / canvasHeight) * 2 + 1;
      //创建一个射线投射器`Raycaster`
      const raycaster = new THREE.Raycaster();
      //.setFromCamera()计算射线投射器`Raycaster`的射线属性.ray
      // 形象点说就是在点击位置创建一条射线，射线穿过的模型代表选中
      raycaster.setFromCamera(new THREE.Vector2(x, y), camera);
      //.intersectObjects([mesh1, mesh2, mesh3])对参数中的网格模型对象进行射线交叉计算
      // 未选中对象返回空数组[],选中一个对象，数组1个元素，选中两个对象，数组两个元素
      const intersects = raycaster.intersectObjects(objView);
      // intersects.length大于0说明，说明选中了模型
      if (intersects.length > 0) {
        transformControl.detach()
        // 选中模型的第一个模型，设置为红色
        // intersects[0].object.material.color.set(0xff0000);
        const obj: any = intersects[0].object;
        if (obj.ancestors != null) {
          if (objCtrlRef.focusObj !== null && objCtrlRef.focusObj.id === obj.ancestors.id) {
            objCtrlRef.focusObj = null
          } else {
            objCtrlRef.focusObj = (obj.ancestors)
            transformControl.attach(obj.ancestors)
          }
        } else if (obj.parent instanceof DropInViewer) {
          if (objCtrlRef.focusObj !== null && objCtrlRef.focusObj.id === obj.parent.id) {
            objCtrlRef.focusObj = null
          } else {
            objCtrlRef.focusObj = (obj.parent)
            transformControl.attach(obj.parent)
          }
        } else {
          if (objCtrlRef.focusObj !== null && objCtrlRef.focusObj.id === obj.id) {
            objCtrlRef.focusObj = null
          } else {
            objCtrlRef.focusObj = (obj)
            transformControl.attach(obj)
          }
        }
      } else {
        objCtrlRef.focusObj = null
        transformControl.detach()
      }
    })


  }

  let animationFrameR: number | null = null;
  let rotateAngleR = 0; // 用于圆周运动计算的角度值
  let rotateR = 2; // 相机圆周运动的半径
  function startRotate() {
    cameraCtrlRef.isRotate = true;
    // R = Math.sqrt(Math.sqrt(camera.position.x * camera.position.x + camera.position.z * camera.position.z) + camera.position.y * camera.position.y);


    // rotateR = Math.sqrt(camera.position.x * camera.position.x + camera.position.z * camera.position.z);



    // const x = camera.position.x - _cameraLookAt.x
    // const z = camera.position.z - _cameraLookAt.z

    // const x = camera.position.x 
    // const z = camera.position.z

    const x = Math.abs(camera.position.x - _cameraLookAt.x)
    const z = Math.abs(camera.position.z - _cameraLookAt.z)


    // controls.enableDamping=true
    // controls.dampingFactor = 0.05

    rotateR = Math.sqrt(x * x + z * z);

    rotate();
  }
  function rotate() {
    rotateAngleR += 0.01;
    // 相机y坐标不变，在XOZ平面上做圆周运动
    camera.position.x = rotateR * Math.cos(rotateAngleR) + _cameraLookAt.x;
    camera.position.z = rotateR * Math.sin(rotateAngleR) + _cameraLookAt.z;
    camera.lookAt(_cameraLookAt);
    renderer.render(scene, camera);
    animationFrameR = requestAnimationFrame(rotate);
  }
  function stopRotate() {
    if (animationFrameR !== null) {
      cancelAnimationFrame(animationFrameR);
    }
    animationFrameR = null;
    cameraCtrlRef.isRotate = false;
  }
  function transformControlSetMode(mode: 'translate' | 'rotate' | 'scale') {
    objCtrlRef.modeType = mode
    transformControl.setMode(mode);
  }

  /**
   * 渲染环境贴图
   *
   * @param url 路径
   */
  function renderHdr(url: string) {
    // 环境贴图
    const rgbeLoader = new RGBELoader();
    rgbeLoader.load(
      url,
      texture => {
        const pmremGenerator = new THREE.PMREMGenerator(renderer);
        pmremGenerator.compileEquirectangularShader();
        const envMap = pmremGenerator.fromEquirectangular(texture).texture;
        // 设备为背景（也可以用其他的的场景背景）
        scene.background = envMap;
        // 设为场景中所有物理材质的环境贴图
        scene.environment = envMap;
        texture.dispose();
        pmremGenerator.dispose();
      },
      undefined,
      error => {
        console.error('Error loading HDR texture', error);
      }
    );
  }

  /**
   * glb 和 gltf 渲染
   *
   * @param url 地址
   */
  function renderGltf(url: string, cb: Function) {
    const loader: GLTFLoader = new GLTFLoader();
    progressState.value = true;
    loader.load(
      url,
      async (gltf: GLTF) => {
        // 解决模型为黑色的问题
        gltf.scene.traverse(function (child: any) {
          if (child.isMesh) {
            child.material.emissive = child.material.color;
            child.material.emissiveMap = child.material.map;
            child.castShadow = true;
            child.receiveShadow = true;
            child.ancestors = gltf.scene;
          }
        });
        gltf.scene.castShadow = true;

        if (typeof cb === 'function') {
          await cb(gltf);
        }
        scene.add(gltf.scene);

        objView.push(gltf.scene)
        progressState.value = false;
      },
      function (xhr) {
        // 控制台查看加载进度xhr
        console.log(Math.floor((xhr.loaded / xhr.total) * 100));
        progressPercent.value = Math.floor(xhr.loaded / xhr.total);
      }
    );
  }

  /**
   * ply点云文件渲染
   *
   * @param url 地址
   */
  function renderPly(url: string) {
    const loader = new PLYLoader();
    progressState.value = true;
    loader.load(
      url,
      geometry => {
        // const material = new THREE.PointsMaterial({
        //     color: 0xffffff,
        //     size: 0.4,
        //     opacity: 0.6,
        //     transparent: true,
        //     blending: THREE.AdditiveBlending,
        //     map: generateSprite()
        // })

        // const textureLoader = new THREE.TextureLoader();
        // textureLoader.load('texture.jpg', texture => {
        //     // 应用纹理
        //     const material = new THREE.MeshPhongMaterial({ map: texture });
        //     const mesh = new THREE.Mesh(geometry, material);
        //     scene.add(mesh);

        //     // 渲染场景
        //     render();
        // });

        // 创建粒子系统
        // const mesh = new THREE.Points(geometry, material)
        // scene.add(mesh)
        const material = new THREE.MeshPhongMaterial({
          color: 0xff0000,
          shininess: 20 // 高光部分的亮度，默认30
        });
        geometry.computeBoundingBox();
        geometry.center();
        geometry.computeVertexNormals();
        const mesh = new THREE.Mesh(geometry, material);

        scene.add(mesh);
        progressState.value = false;
        objView.push(mesh)
      },
      function (xhr) {
        // 控制台查看加载进度xhr
        progressPercent.value = Math.floor(xhr.loaded / xhr.total);
      }
    );
  }



  /**
   * 3d高斯渲染
   *
   * @param netUrl 网路地址
   */
  function render3DGS(netUrl: string) {
    gsView = new DropInViewer({
      gpuAcceleratedSort: true,
      sharedMemoryForWorkers: false,
      // 'showLoadingUI': true,
    });

    gsView
      .addSplatScene(
        netUrl,
        // fileUrl,
        {
          splatAlphaRemovalThreshold: 5,
          showLoadingSpinner: true,
          sharedMemoryForWorkers: true,
          format: 2
        }
      )
      .then(() => {
        const geometry = gsView.children[0].geometry;

        geometry.computeBoundingBox();
        geometry.center();
        geometry.computeVertexNormals();
        // viewer.rotation.x = Math.PI;

        // viewer.splatMesh.scenes.traverse(function (child) {
        //     if (child.isMesh) {
        //         child.material.emissive = child.material.color;
        //         child.material.emissiveMap = child.material.map;
        //     }
        // });

        // gltf.scene.traverse(function (child) {
        //     if (child.isMesh) {
        //         child.material.emissive = child.material.color;
        //         child.material.emissiveMap = child.material.map;
        //         child.castShadow = true;
        //         child.receiveShadow = true
        //     }
        // });

        gsView.castShadow = true;
        // viewer.receiveShadow = true

        scene.add(gsView);
        objView.push(gsView);
      });
  }

  // 清空场景中的所有对象
  function clearScene() {
    if (scene !== null) {
      objView = []
      while (scene.children.length > 0) {
        const object = scene.children[0];

        if (object.isMesh) {
          // 如果有几何体和材质，也进行清理
          object.geometry.dispose();
          if (object.material.isMaterial) {
            cleanMaterial(object.material);
          } else {
            // 材质组
            for (const material of object.material) cleanMaterial(material);
          }
        }

        scene.remove(object);
      }

      // 清空HDR
      if (scene.background) {
        scene.background.dispose();
        scene.background = null;
      }
      if (scene.environment) {
        scene.environment.dispose();
        scene.environment = null;
      }
      if (transformControl) {
        transformControl.dispose();
        transformControl = null;
      }
    }
  }

  // 清理材质资源
  function cleanMaterial(material: any) {
    material.dispose(); // 清理材质
    if (material.map) material.map.dispose(); // 清理贴图
    if (material.lightMap) material.lightMap.dispose();
    if (material.bumpMap) material.bumpMap.dispose();
    if (material.normalMap) material.normalMap.dispose();
    if (material.specularMap) material.specularMap.dispose();
    if (material.envMap) material.envMap.dispose();
    // ... 其他你使用的材质资源
  }

  /** 自动渲染 */
  function animate() {
    renderer.render(scene, camera);
    requestAnimationFrame(animate);
  }

  function cameraLookAt3dGs() {
    // console.log(gsView);
    _cameraLookAt = gsView.position;
    camera.lookAt(_cameraLookAt)
  }


  const keyState: any = {
    KeyW: false,
    KeyS: false,
    KeyA: false,
    KeyD: false,
  };
  function cameraKeyUp(event: any) {
    keyState[event.code] = false;
    updateMoveDirection();
  }

  function cameraKeyDown(event: any) {
    keyState[event.code] = true;
    updateMoveDirection();
  }

  const moveSpeed = 0.1
  function updateMoveDirection() {
    const direction = new THREE.Vector3(); // 存储相机前方方向
    const moveDirection = new THREE.Vector3(); // 计算移动向量
    const upVector = new THREE.Vector3(0, 1, 0); // 作为旋转轴辅助计算

    // 获取相机面向的方向
    camera.getWorldDirection(direction);

    // 根据按键状态调整移动向量
    if (keyState['KeyW']) moveDirection.add(direction);
    if (keyState['KeyS']) moveDirection.sub(direction);
    if (keyState['KeyA']) moveDirection.add(upVector.clone().cross(direction)); // 左转
    if (keyState['KeyD']) moveDirection.sub(upVector.clone().cross(direction)); // 右转

    // 确保移动向量有明确的方向，避免无效移动
    moveDirection.normalize();

    // 应用移动，乘以速度常量控制速度
    camera.position.add(moveDirection.multiplyScalar(moveSpeed));
  }

  function cameraStartKeyCtrl() {
    document.addEventListener('keydown', cameraKeyDown, false);
    document.addEventListener('keyup', cameraKeyUp, false);
    cameraCtrlRef.isMove = true
  }

  function cameraStopKeyCtrl() {
    document.removeEventListener('keydown', cameraKeyDown, false);
    document.removeEventListener('keyup', cameraKeyUp, false);
    cameraCtrlRef.isMove = false
  }

  return {
    cameraRef,
    sceneRef,
    rendererRef,
    progressState,
    progressPercent,
    cameraCtrlRef,
    objCtrlRef,
    startRotate,
    stopRotate,
    renderPly,
    render3DGS,
    renderGltf,
    renderHdr,
    clearScene,
    animate,
    resize,

    transformControlSetMode, cameraLookAt3dGs,
    cameraStartKeyCtrl,
    cameraStopKeyCtrl
  };
}
