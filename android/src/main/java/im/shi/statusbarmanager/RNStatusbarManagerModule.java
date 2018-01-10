
package im.shi.statusbarmanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 调用此方法，主题不能设置为fitsSystemWindow = true;
 * 此方法调用后 应用为全屏(NavigationBar还是存在)，透明状态栏
 */
public class RNStatusbarManagerModule extends ReactContextBaseJavaModule {
    /**
     * 默认透明
     */
    private static final int DEFAULT_STATUS_BAR_ALPHA = 0x00;
    /**
     * 默认白色
     */
    private static final int DEFAULT_STATUS_BAR_WHITE = 0xff;

    public static enum ROM_TYPE {
        MIUI,
        FLYME,
        INVALID,
        OTHER
    }

    public static ROM_TYPE OS_MODULE = ROM_TYPE.INVALID;
    private static int STATUS_BAR_HEIGHT = 0;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Resources resources = Resources.getSystem();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                STATUS_BAR_HEIGHT = resources.getDimensionPixelSize(resourceId);
            }
        }
    }

    public RNStatusbarManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNStatusbarManager";
    }

    /**
     * 全屏但不隐藏NavigationBar，透明状态栏
     *
     * @param activity
     */
    public static void translucentStatusBar(Activity activity, boolean isDark) {
        Window window = activity.getWindow();
        //状态栏透明化
        setTranslucentStatus(window);
        //设置状态栏图标和字体颜色、
        boolean lightMode = false;
        if (OS_MODULE.equals(ROM_TYPE.INVALID)) {
            // 适配魅族
            lightMode = setImmersiveFlyme(window, isDark);
            if (lightMode) {
                //设置为魅族
                OS_MODULE = ROM_TYPE.FLYME;
            } else {
                // 适配小米
                lightMode = setImmersiveMiUi(window, isDark);
                if (lightMode) {
                    //设置为小米
                    OS_MODULE = ROM_TYPE.MIUI;
                } else {
                    //既不是小米也不是魅族
                    OS_MODULE = ROM_TYPE.OTHER;
                    setImmersiveMOS(window, isDark);
                }

            }
        } else if (OS_MODULE.equals(ROM_TYPE.FLYME)) {
            setImmersiveFlyme(window, isDark);
        } else if (OS_MODULE.equals(ROM_TYPE.MIUI)) {
            setImmersiveMiUi(window, isDark);
        } else {
            setImmersiveMOS(window, isDark);
        }
    }

    /**
     * 沉浸与否，如果沉浸则使用默认色和默认透明度
     * 如果沉浸，则会自定义一个statusbarView
     * 不沉浸，则会有一个paddingTop，高度为statusbar，颜色布局内文件设置
     *
     * @param activity
     * @param currentRootView
     * @param needStatusBarView 沉浸与否，
     */
    public static void steepStatusbarView(@NonNull Activity activity, @NonNull View currentRootView, boolean needStatusBarView) {
        if (needStatusBarView) {
            steepStatusbarView(activity, currentRootView, DEFAULT_STATUS_BAR_WHITE, DEFAULT_STATUS_BAR_ALPHA);
        } else {
            steepStatusbarView(activity, currentRootView);
        }
    }

    /**
     * 沉浸
     * <p>
     * 用接口先获取activity的contentView，在获取contentView的父view parent，将contentView从parent移除，新建一个container
     * ，把自建的statubar和contentView一起放在新建一个container中，再把container放到parent中
     *
     * @param activity
     * @param color          沉浸颜色
     * @param statusBarAlpha 沉浸透明度
     */
    public static void steepStatusbarView(@NonNull Activity activity, @NonNull View viewGroup, @ColorInt int color,
                                          @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (STATUS_BAR_HEIGHT > 0) {
            InitStatusbarView initStatusbarView = new InitStatusbarView(activity, (ViewGroup) viewGroup).invoke();
            FrameLayout containerFrame = initStatusbarView.getContainerFrame();
            ViewGroup currentRootView = initStatusbarView.getCurrentRootView();

            View statusBarView = new View(activity);
            statusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            statusBarView.setFitsSystemWindows(false);
            ViewGroup.LayoutParams rootLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            containerFrame.addView(currentRootView, rootLp);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    STATUS_BAR_HEIGHT);
            containerFrame.addView(statusBarView, params);
        }
    }

    /**
     * 沉浸
     * <p>
     * 用接口先获取activity的contentView，在获取contentView的父view parent，将contentView从parent移除，新建一个container
     * ，把自建的statubar和contentView一起放在新建一个container中，再把container放到parent中
     *
     * @param activity
     * @param viewGroup
     * @param color
     */
    public static void steepStatusbarView(@NonNull Activity activity, @NonNull View viewGroup, @ColorRes int color) {
        if (STATUS_BAR_HEIGHT > 0) {
            InitStatusbarView initStatusbarView = new InitStatusbarView(activity, (ViewGroup) viewGroup).invoke();
            FrameLayout containerFrame = initStatusbarView.getContainerFrame();
            ViewGroup currentRootView = initStatusbarView.getCurrentRootView();

            View statusBarView = new View(activity);
            if (color <= 0) {
                statusBarView.setBackgroundResource(android.R.color.white);
            } else {
                statusBarView.setBackgroundResource(color);
            }
            statusBarView.setFitsSystemWindows(false);
            ViewGroup.LayoutParams rootLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            containerFrame.addView(currentRootView, rootLp);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    STATUS_BAR_HEIGHT);
            containerFrame.addView(statusBarView, params);
        }
    }

    /**
     * 沉浸
     * <p>
     * 用接口先获取activity的contentView，在获取contentView的父view parent，将contentView从parent移除，新建一个container
     * ，把自建的statubar和contentView一起放在新建一个container中，再把container放到parent中
     *
     * @param activity
     * @param viewGroup
     */
    private static void steepStatusbarView(@NonNull Activity activity, @NonNull View viewGroup) {
        if (STATUS_BAR_HEIGHT > 0) {
            InitStatusbarView initStatusbarView = new InitStatusbarView(activity, (ViewGroup) viewGroup);
            initStatusbarView.invokeNoStarusbar();
        }
    }

    private static void setTranslucentStatus(Window window) {
        if (OS_MODULE.equals(ROM_TYPE.MIUI) || OS_MODULE.equals(ROM_TYPE.FLYME)) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上系统状态栏透明
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
                // ZUK Z1是个另类，自家应用可以实现字体颜色变色，但没开放接口
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                // android 5不能修改状态栏字体颜色，因此直接用FLAG_TRANSLUCENT_STATUS，nexus表现为半透明
                // update: 部分手机运用FLAG_TRANSLUCENT_STATUS时背景不是半透明而是没有背景了。。。。。
                // window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // 采取setStatusBarColor的方式，部分机型不支持，那就纯黑了，保证状态栏图标可见
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(0);
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean setImmersiveFlyme(Window window, boolean dark) {
        // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
        setImmersiveMOS(window, dark);
        //旧方案
        boolean result = false;
        if (window != null) {
            try {
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                WindowManager.LayoutParams lp = window.getAttributes();
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean setImmersiveMiUi(Window window, boolean dark) {
        // MIUI Android 6.0 ，开发版 7.7.13 及以后版本 支持了 Android 官方的实现方案，旧的方案失效
        setImmersiveMOS(window, dark);

        //旧方案
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                    //状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);
                    //清除黑色字体
                }
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 着色
     *
     * @param window
     * @param isFontColorDark
     */
    public static void setImmersiveMOS(Window window, boolean isFontColorDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFontColorDark) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                int uiVisibility = window.getDecorView().getSystemUiVisibility();
                window.getDecorView().setSystemUiVisibility(uiVisibility);
            }
        }
    }


    private static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (color < 0) {
            color = DEFAULT_STATUS_BAR_WHITE;
        }
        if (alpha <= 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    private static class InitStatusbarView {
        private Activity activity;
        private ViewGroup viewGroup;
        private ViewGroup currentRootView;
        private FrameLayout containerFrame;

        public InitStatusbarView(Activity activity, ViewGroup viewGroup) {
            this.activity = activity;
            this.viewGroup = viewGroup;
        }

        public ViewGroup getCurrentRootView() {
            return currentRootView;
        }

        public FrameLayout getContainerFrame() {
            return containerFrame;
        }

        public InitStatusbarView invoke() {
            currentRootView = viewGroup;
            currentRootView.setFitsSystemWindows(false);
            currentRootView.setPadding(currentRootView.getPaddingLeft(),
                    currentRootView.getTop() + STATUS_BAR_HEIGHT, currentRootView.getPaddingRight()
                    , currentRootView.getPaddingBottom());
            currentRootView.getLayoutParams().height += STATUS_BAR_HEIGHT;

            containerFrame = new FrameLayout(activity);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            containerFrame.setLayoutParams(layoutParams);
            containerFrame.setFitsSystemWindows(false);

            FrameLayout parent = (FrameLayout) currentRootView.getParent();
            parent.removeView(currentRootView);
            parent.addView(containerFrame);
            parent.setFitsSystemWindows(false);
            return this;
        }

        public InitStatusbarView invokeNoStarusbar() {
            currentRootView = viewGroup;
            currentRootView.setFitsSystemWindows(false);
            int paddingLeft = currentRootView.getPaddingLeft();
            int top = currentRootView.getTop();
            int paddingRight = currentRootView.getPaddingRight();
            int paddingBottom = currentRootView.getPaddingBottom();
            FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                    , FrameLayout.LayoutParams.MATCH_PARENT);
            currentRootView.setLayoutParams(rootLp);
            currentRootView.setPadding(paddingLeft,
                    top + STATUS_BAR_HEIGHT, paddingRight
                    , paddingBottom);

            return this;
        }
    }
}
