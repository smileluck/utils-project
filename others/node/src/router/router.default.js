const Router = require("koa-router");
const { encryptByAESPKCS7, sha256 } = require("@/utils/crypto")

const router = new Router({});


//GET /users/
router.get("/", (ctx, next) => {

    const plainStr = 'plainstr'

    console.log(plainStr)

    const aesStr = encryptByAESPKCS7(plainStr, "appsecretKey", "iv")
    // console.log(aesStr)

    const shaStr = sha256(aesStr);
    // console.log(shaStr)


    ctx.body = shaStr;
});

module.exports = router
