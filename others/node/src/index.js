const Koa = require('koa');
const app = new Koa();
require('module-alias/register')


const { PORT } = require("@config/config.default.js");
const userRouter = require("@/router/router.default.js");


app
  .use(userRouter.routes());

app.listen(PORT);