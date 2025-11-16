import { WaveAnimation } from "@/ui-frontend/common/WaveAnimation";

export const Banner = () => {
  return (
    //{/**注意上面要加入before选择器，让grid这个模糊图片盖上原来图片，从而形成新样式的背景图 */}
    <div
      id="banner-wrapper"
      className="relative overflow-hidden h-auto z-0 before:content-[''] before:absolute before:inset-0 before:z-3 before:bg-fixed 
      before:bg-[url('statics/images/grid.png')]"
    >
      {/**三条波浪 */}
      <div
        id="wave-wrapper"
        className="absolute overflow-hidden inset-0 m-auto"
      >
        <WaveAnimation
          zIndex="z-10"
          opacity={50}
          imageUrl="statics/images/wave-top.png"
          waveWidth="50%"
          waveHeight="100px"
          animationDuration=""
        />
        <WaveAnimation
          zIndex="z-15"
          opacity={70}
          imageUrl="statics/images/wave-mid.png"
          waveWidth="50%"
          waveHeight="120px"
          animationDuration="15s linear infinite"
        />
        <WaveAnimation
          zIndex="z-20"
          opacity={80}
          imageUrl="statics/images/wave-bot.png"
          waveWidth="50%"
          waveHeight="100px"
          animationDuration="25s linear infinite"
        />
      </div>

      {/**TODO 顶部向下动画 尚未添加事件 待定 */}
      <div id="header-top-down" className=""></div>
      {/**内容模块 Center Content https://api.mtyqx.cn/api/random.php */}
      <div
        id="center"
        className="w-full h-screen bg-center-top bg-fixed bg-no-repeat bg-cover -z-10
          bg-[url('https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列3')]"
      >
        <div
          id="focus-info"
          className="relative max-w-200 p-2.5 top-1/2 left-1/2 -translate-1/2 text-center
           z-100 transition-all duration-400 ease-in-out md:block hidden"
        >
          <h1 className="glitch" data-text="您好">
            您好
          </h1>
          {/**对您好下面的黑色方框（带有箭头）绘制 */}
          {/* <div
            id="header-box-info"
            className="relative w-[65%] m-auto text-[#eaeadf] bg-black/50
              p-4 mt-6 leading-7.5 rounded-2xl box-content whitespace-nowrap before:content-['']
              before:absolute before:-top-[29px] before:left-2/5 before:-ml-2.5 before:border-15 
              before:border-solid before:border-transparent before:border-b-black/50"
          >
            <p className="font-bold">
              <span className="mx-2.5">
                {"You got to put the past behind you before you can move on."}
              </span>
            </p>
            <div
              id="header-social"
              className="h-9 -mb-2.5 list-none inline-block"
            >
              <li className="relative float-left mx-1.5">
                <img
                  className="h-9 w-9 p-1.5 -scale-100"
                  src="/statics/images/next-b.svg"
                  alt=""
                />
              </li>
              <li className="relative float-left mx-1.5">
                <img
                  className="h-9 w-9 p-1.5"
                  src="/statics/images/next-b.svg"
                  alt=""
                />
              </li>
            </div>
          </div> */}
        </div>
      </div>
    </div>
  );
};
