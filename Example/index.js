import {
    NativeModules
} from 'react-native';

const RNStatusbarManager = NativeModules.RNStatusbarManager;

function translucentStatusBar(isDark) {
    RNStatusbarManager && RNStatusbarManager.translucentStatusBar && RNStatusbarManager.translucentStatusBar(isDark);
}

function steepOrPadding(currentRootView, needStatusBarView) {
    RNStatusbarManager && RNStatusbarManager.steepOrPadding && RNStatusbarManager.steepOrPadding(currentRootView, needStatusBarView);
}

function steepWithConfigColorRes(currentRootView, colorRes) {
    RNStatusbarManager && RNStatusbarManager.steepWithConfigColorRes && RNStatusbarManager.steepWithConfigColorRes(currentRootView, colorRes);
}

function steepWithConfigAll(currentRootView, colorInt, statusBarAlpha) {
    RNStatusbarManager && RNStatusbarManager.steepWithConfigAll && RNStatusbarManager.steepWithConfigAll(currentRootView, colorInt, statusBarAlpha);
}

export {
    translucentStatusBar,
    steepOrPadding,
    steepWithConfigColorRes,
    steepWithConfigAll,
};
