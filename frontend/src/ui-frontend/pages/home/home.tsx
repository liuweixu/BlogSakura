import { Banner } from "./components/banner";
import { Feature } from "./components/feature";
import { ListWrapper } from "./components/list_articles";
import { RankingList } from "./components/ranking_list";
function App() {
  return (
    <div className="w-full">
      <Banner />
      {/**MainWrapper
       * 最大宽度为900px、
       * mx-auto为居中显示
       */}
      <div
        className="w-full max-w-225 px-2.5 mx-auto"
        id="content"
        style={{
          animation: "main 1s",
        }}
      >
        <Feature />
        <div className="w-full h-auto mt-14 inline-block">
          <h1 className="text-[#666] font-bold mt-2.5 mb-7.5 border-b border-dashed border-[#ececec]">
            <i className="iconfont icon-envira" />
            <span> Discovery</span>
          </h1>
        </div>
        <div className="flex flex-col lg:flex-row items-start gap-8">
          <div className="w-full lg:w-[72%]">
            <ListWrapper />
          </div>
          <div className="w-full lg:w-[28%]">
            <RankingList />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
