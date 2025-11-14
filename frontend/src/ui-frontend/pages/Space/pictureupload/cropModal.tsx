import {
  useRef,
  useState,
  forwardRef,
  useImperativeHandle,
  useEffect,
  useMemo,
  useCallback,
} from "react";
import ReactCrop from "react-image-crop";
import { Modal, Button, Space, message } from "antd";
import "react-image-crop/dist/ReactCrop.css";
import PictureEditWebSocket from "../common/pictureeditwebsocket";
import {
  PICTURE_EDIT_MESSAGE_TYPE_ENUM,
  PICTURE_EDIT_ACTION_ENUM,
} from "../common/picture";
import { sessionLoginUser } from "@/api/userController";

interface Props {
  onConfirm?: (blob: Blob) => void;
}

export interface CropModalRef {
  open: (url: string, pictureId?: number) => void;
}

const CropModal = forwardRef<CropModalRef, Props>((props, ref) => {
  const [visible, setVisible] = useState(false);
  const [imgSrc, setImgSrc] = useState<string>();
  const [pictureId, setPictureId] = useState<number | undefined>(undefined);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [crop, setCrop] = useState<any>({
    unit: "%",
    width: 60,
    aspect: 1,
  });

  const imgRef = useRef<HTMLImageElement | null>(null);
  const [scale, setScale] = useState(1);
  const [rotate, setRotate] = useState(0);
  const [loading, setLoading] = useState(false);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [completedCrop, setCompletedCrop] = useState<any>(null);

  // 实时编辑相关状态
  const [loginUser, setLoginUser] = useState<API.UserVO>();
  const [editingUser, setEditingUser] = useState<API.UserVO | undefined>();
  const websocketRef = useRef<PictureEditWebSocket | null>(null);

  // 没有用户正在编辑中，可进入编辑
  const canEnterEdit = useMemo(() => {
    return !editingUser;
  }, [editingUser]);

  // 正在编辑的用户是本人，可退出编辑
  const canExitEdit = useMemo(() => {
    return editingUser?.id === loginUser?.id;
  }, [editingUser, loginUser]);

  // 可以编辑
  const canEdit = useMemo(() => {
    return editingUser?.id === loginUser?.id;
  }, [editingUser, loginUser]);

  // 获取登录用户信息
  useEffect(() => {
    const getUserInfo = async () => {
      const res = await sessionLoginUser();
      if (res?.data.code === 0 && res?.data.data) {
        setLoginUser(res.data.data);
      }
    };
    getUserInfo();
  }, []);

  useImperativeHandle(ref, () => ({
    open: (url: string, pictureId?: number) => {
      setImgSrc(url);
      setPictureId(pictureId);
      setVisible(true);
      // 重置状态
      setScale(1);
      setRotate(0);
      setCompletedCrop(null);
      setEditingUser(undefined);
      setCrop({
        unit: "%",
        width: 60,
        aspect: 1,
      });
    },
  }));

  const onImageLoaded = (img: HTMLImageElement) => {
    imgRef.current = img;
  };

  const getCroppedBlob = () => {
    const image = imgRef.current!;
    if (!image) {
      throw new Error("Image not loaded");
    }

    // 使用 completedCrop 或当前的 crop
    const currentCrop = completedCrop || crop;

    // 如果没有选择裁剪区域，使用整个图片
    if (!currentCrop || !currentCrop.width || !currentCrop.height) {
      // 创建一个临时 canvas 来应用旋转和缩放
      const tempCanvas = document.createElement("canvas");
      const tempCtx = tempCanvas.getContext("2d")!;

      // 计算旋转后的尺寸
      const rad = (rotate * Math.PI) / 180;
      const cos = Math.abs(Math.cos(rad));
      const sin = Math.abs(Math.sin(rad));
      const newWidth = image.naturalWidth * cos + image.naturalHeight * sin;
      const newHeight = image.naturalWidth * sin + image.naturalHeight * cos;

      tempCanvas.width = newWidth * scale;
      tempCanvas.height = newHeight * scale;

      tempCtx.translate(tempCanvas.width / 2, tempCanvas.height / 2);
      tempCtx.rotate(rad);
      tempCtx.scale(scale, scale);
      tempCtx.drawImage(
        image,
        -image.naturalWidth / 2,
        -image.naturalHeight / 2
      );

      return new Promise<Blob>((resolve, reject) => {
        tempCanvas.toBlob((blob) => {
          if (blob) resolve(blob);
          else reject(new Error("Failed to create blob"));
        }, "image/png");
      });
    }

    // 获取图片的实际显示尺寸（不包含 transform）
    const displayWidth = image.offsetWidth || image.naturalWidth;
    const displayHeight = image.offsetHeight || image.naturalHeight;

    // 计算裁剪区域在原始图片中的实际位置和尺寸
    let cropX: number;
    let cropY: number;
    let cropWidth: number;
    let cropHeight: number;

    if (currentCrop.unit === "%") {
      // 百分比单位：需要转换为像素
      cropX = (currentCrop.x / 100) * displayWidth;
      cropY = (currentCrop.y / 100) * displayHeight;
      cropWidth = (currentCrop.width / 100) * displayWidth;
      cropHeight = (currentCrop.height / 100) * displayHeight;
    } else {
      // 像素单位
      cropX = currentCrop.x;
      cropY = currentCrop.y;
      cropWidth = currentCrop.width;
      cropHeight = currentCrop.height;
    }

    // 计算显示尺寸和原始尺寸的比例
    const scaleX = image.naturalWidth / displayWidth;
    const scaleY = image.naturalHeight / displayHeight;

    // 转换为原始图片的坐标
    const sourceX = cropX * scaleX;
    const sourceY = cropY * scaleY;
    const sourceWidth = cropWidth * scaleX;
    const sourceHeight = cropHeight * scaleY;

    // 先创建一个 canvas，应用旋转和缩放
    const tempCanvas = document.createElement("canvas");
    const tempCtx = tempCanvas.getContext("2d")!;

    // 计算旋转后的尺寸
    const rad = (rotate * Math.PI) / 180;
    const cos = Math.abs(Math.cos(rad));
    const sin = Math.abs(Math.sin(rad));
    const rotatedWidth = image.naturalWidth * cos + image.naturalHeight * sin;
    const rotatedHeight = image.naturalWidth * sin + image.naturalHeight * cos;

    tempCanvas.width = rotatedWidth * scale;
    tempCanvas.height = rotatedHeight * scale;

    // 应用旋转和缩放
    tempCtx.translate(tempCanvas.width / 2, tempCanvas.height / 2);
    tempCtx.rotate(rad);
    tempCtx.scale(scale, scale);
    tempCtx.drawImage(image, -image.naturalWidth / 2, -image.naturalHeight / 2);

    // 计算裁剪区域在旋转后的 canvas 中的位置
    // 将原始图片坐标转换为旋转后的坐标
    const centerX = image.naturalWidth / 2;
    const centerY = image.naturalHeight / 2;

    // 裁剪区域的中心点相对于图片中心
    const cropCenterX = sourceX + sourceWidth / 2 - centerX;
    const cropCenterY = sourceY + sourceHeight / 2 - centerY;

    // 旋转后的中心点坐标
    const rotatedCenterX =
      cropCenterX * Math.cos(rad) - cropCenterY * Math.sin(rad);
    const rotatedCenterY =
      cropCenterX * Math.sin(rad) + cropCenterY * Math.cos(rad);

    // 在旋转后的 canvas 中的位置
    const finalCenterX = tempCanvas.width / 2 + rotatedCenterX * scale;
    const finalCenterY = tempCanvas.height / 2 + rotatedCenterY * scale;

    // 创建最终的裁剪 canvas
    const finalCanvas = document.createElement("canvas");
    finalCanvas.width = sourceWidth * scale;
    finalCanvas.height = sourceHeight * scale;
    const finalCtx = finalCanvas.getContext("2d")!;

    // 从临时 canvas 中裁剪出选中的区域
    finalCtx.drawImage(
      tempCanvas,
      finalCenterX - (sourceWidth * scale) / 2,
      finalCenterY - (sourceHeight * scale) / 2,
      sourceWidth * scale,
      sourceHeight * scale,
      0,
      0,
      sourceWidth * scale,
      sourceHeight * scale
    );

    return new Promise<Blob>((resolve, reject) => {
      finalCanvas.toBlob((blob) => {
        if (blob) resolve(blob);
        else reject(new Error("Failed to create blob"));
      }, "image/png");
    });
  };

  // 初始化 WebSocket 连接，绑定事件
  const initWebsocket = useCallback(() => {
    if (!pictureId || !visible) {
      return;
    }

    // 防止之前的连接未释放
    if (websocketRef.current) {
      websocketRef.current.disconnect();
    }

    // 创建 WebSocket 实例
    websocketRef.current = new PictureEditWebSocket(pictureId);

    // 建立 WebSocket 连接
    websocketRef.current.connect();

    // 监听通知消息
    websocketRef.current.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.INFO, (msg) => {
      console.log("收到通知消息：", msg);
      message.info(msg.message);
    });

    // 监听错误消息
    websocketRef.current.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ERROR, (msg) => {
      console.log("收到错误消息：", msg);
      message.error(msg.message);
    });

    // 监听进入编辑状态消息
    websocketRef.current.on(
      PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT,
      (msg) => {
        console.log("收到进入编辑状态消息：", msg);
        message.info(msg.message);
        setEditingUser(msg.user);
      }
    );

    // 监听编辑操作消息
    websocketRef.current.on(
      PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION,
      (msg) => {
        console.log("收到编辑操作消息：", msg);
        // 只显示消息，不重复提示（因为操作已经同步执行）
        if (msg.message) {
          message.info(msg.message);
        }
        switch (msg.editAction) {
          case PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT:
            setRotate((r) => r - 90);
            break;
          case PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT:
            setRotate((r) => r + 90);
            break;
          case PICTURE_EDIT_ACTION_ENUM.ZOOM_IN:
            setScale((s) => s + 0.2);
            break;
          case PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT:
            setScale((s) => Math.max(0.2, s - 0.2));
            break;
        }
      }
    );

    // 监听退出编辑状态消息
    websocketRef.current.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT, (msg) => {
      console.log("收到退出编辑状态消息：", msg);
      message.info(msg.message);
      setEditingUser(undefined);
    });
  }, [pictureId, visible]);

  // 监听 visible 和 pictureId 变化，初始化 WebSocket
  useEffect(() => {
    if (visible && pictureId) {
      initWebsocket();
    }

    return () => {
      // 断开连接
      if (websocketRef.current) {
        websocketRef.current.disconnect();
        websocketRef.current = null;
      }
      setEditingUser(undefined);
    };
  }, [visible, pictureId, initWebsocket]);

  // 关闭弹窗
  const closeModal = () => {
    setVisible(false);
    // 断开连接
    if (websocketRef.current) {
      websocketRef.current.disconnect();
      websocketRef.current = null;
    }
    setEditingUser(undefined);
  };

  // 进入编辑状态
  const enterEdit = () => {
    if (websocketRef.current) {
      // 发送进入编辑状态的消息
      websocketRef.current.sendMessage({
        type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT,
      });
    }
  };

  // 退出编辑状态
  const exitEdit = () => {
    if (websocketRef.current) {
      // 发送退出编辑状态的消息
      websocketRef.current.sendMessage({
        type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT,
      });
    }
  };

  // 编辑图片操作
  const editAction = (action: string) => {
    // 只有在编辑状态下才发送消息
    if (websocketRef.current && canEdit) {
      // 发送编辑操作的请求
      websocketRef.current.sendMessage({
        type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION,
        editAction: action,
      });
    }
  };

  // 向左旋转
  const rotateLeft = () => {
    if (!canEdit) return;
    setRotate((r) => r - 90);
    editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT);
  };

  // 向右旋转
  const rotateRight = () => {
    if (!canEdit) return;
    setRotate((r) => r + 90);
    editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT);
  };

  // 缩放
  const changeScale = (num: number) => {
    if (!canEdit) return;
    if (num > 0) {
      setScale((s) => s + 0.2);
      editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_IN);
    } else {
      setScale((s) => Math.max(0.2, s - 0.2));
      editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT);
    }
  };

  const handleConfirm = async () => {
    setLoading(true);
    const blob = await getCroppedBlob();
    props.onConfirm?.(blob);
    setLoading(false);
    closeModal();
  };

  return (
    <Modal
      open={visible}
      title="编辑图片"
      footer={null}
      onCancel={closeModal}
      width={600}
    >
      {imgSrc && (
        <ReactCrop crop={crop} onChange={setCrop} onComplete={setCompletedCrop}>
          <img
            src={imgSrc}
            alt=""
            crossOrigin="anonymous"
            style={{
              transform: `scale(${scale}) rotate(${rotate}deg)`,
              maxHeight: 400,
            }}
            onLoad={(event) => onImageLoaded(event.currentTarget)}
          />
        </ReactCrop>
      )}

      {/* 协同编辑操作 */}
      {pictureId && (
        <div style={{ marginTop: 16, marginBottom: 16 }}>
          <Space>
            {editingUser && (
              <Button disabled>{editingUser.userName}正在编辑</Button>
            )}
            {canEnterEdit && (
              <Button type="primary" ghost onClick={enterEdit}>
                进入编辑
              </Button>
            )}
            {canExitEdit && (
              <Button danger ghost onClick={exitEdit}>
                退出编辑
              </Button>
            )}
          </Space>
        </div>
      )}

      <Space style={{ marginTop: 16 }}>
        <Button onClick={rotateLeft} disabled={!canEdit}>
          向左旋转
        </Button>
        <Button onClick={rotateRight} disabled={!canEdit}>
          向右旋转
        </Button>
        <Button onClick={() => changeScale(1)} disabled={!canEdit}>
          放大
        </Button>
        <Button onClick={() => changeScale(-1)} disabled={!canEdit}>
          缩小
        </Button>
        <Button
          type="primary"
          loading={loading}
          disabled={!canEdit}
          onClick={handleConfirm}
        >
          确认
        </Button>
      </Space>
    </Modal>
  );
});

export default CropModal;
