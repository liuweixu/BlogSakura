import { healthCheck } from "@/api/healthController";
import { loginUser } from "@/api/userController";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Form } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useForm, type FieldValues } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { message } from "antd";
import { getSpaceVoListByUserId } from "@/api/spaceController";

export function LoginPage() {
  const form = useForm();
  const navigate = useNavigate();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  healthCheck().then((res: any) => {
    console.log("healthCheck");
    console.log(res);
  });
  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(async (fromValue: FieldValues) => {
          //触发异步
          console.log(fromValue);
          // const res = await loginAPI({
          //   userAccount: fromValue.userAccount,
          //   userPassword: fromValue.userPassword,
          // });
          const res = await loginUser({
            userAccount: fromValue.userAccount,
            userPassword: fromValue.userPassword,
          });
          console.log(res.data.message);
          if (
            res?.data.message == "ok" &&
            res?.data.data?.userRole === "admin"
          ) {
            navigate("/backend/"); //跳转至首页
            toast.success("登录成功", {
              description: "Sunday, December 03, 2023 at 9:00 AM",
              action: {
                label: "关闭",
                onClick: () => console.log("Undo"),
              },
            });
          } else if (res.data.data && res.data.data.userRole === "user") {
            message.success("进入私有空间");
            const resSpace = await getSpaceVoListByUserId({
              userId: res.data.data.id,
              spaceType: 0,
            });
            if (resSpace.data.code === 0 && resSpace.data.data) {
              const spaceId = resSpace?.data?.data[0]?.id ?? undefined;
              window.location.href =
                "/personal_space/private_pictures?id=" + spaceId;
            } else {
              message.error("获取私有空间失败");
            }
          } else {
            toast.success("登录失败", {
              description: "Sunday, December 03, 2023 at 9:00 AM",
              action: {
                label: "关闭",
                onClick: () => console.log("Undo"),
              },
            });
          }
        })}
      >
        <Card>
          <CardHeader>
            <CardTitle>登录</CardTitle>
            <CardDescription>请输入账号和密码</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid w-full items-center gap-4">
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="mobile">账号</Label>
                <Input
                  id="userAccount"
                  placeholder="账号"
                  {...form.register("userAccount")}
                />
              </div>
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="code">密码</Label>
                <Input
                  id="userPassword"
                  placeholder="请输入密码"
                  {...form.register("userPassword")}
                />
              </div>
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="code">再次确认密码</Label>
                <Input
                  id="confirmCode"
                  placeholder="请再次输入密码"
                  {...form.register("confirmCode")}
                />
              </div>
            </div>
          </CardContent>
          <CardFooter className="flex justify-between">
            <Button type="submit">确认</Button>
            {/* <Button type="submit" variant="outline">
              注册
            </Button> */}
          </CardFooter>
        </Card>
      </form>
    </Form>
  );
}
