import { Banner } from "./components/banner";
import { Feature } from "./components/feature";
import { ListWrapper } from "./components/list_articles";
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
        <ListWrapper />
      </div>
    </div>
  );
}

export default App;
