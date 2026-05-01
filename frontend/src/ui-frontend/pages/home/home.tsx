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
        <div className="flex flex-col lg:flex-row gap-6">
          <div className="w-full lg:w-3/4">
            <ListWrapper />
          </div>
          <div className="w-full lg:w-1/4 mt-14">
            <RankingList />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
