package vac.yufeng.com.jhotfix;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class JApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);


        //patch.dex表示要修复的class通过dx工具打出来的修复包
        File file = new File(Environment.getExternalStorageDirectory(), "patch.dex");
        if (file.exists()) {

            try {
                //加载补丁包的DexPathList->Element[]
                DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(), Environment.getDataDirectory().getAbsolutePath(),
                        null, getClassLoader());//此处要注意权限声明，测试的时候记得手动在应用管理里面打开存储权限
                Class<?> clazz = dexClassLoader.getClass().getSuperclass();//这里是BaseDexClassLoader，pathList是它的成员变量
                Field pathList = clazz.getDeclaredField("pathList");//获取BaseDexClassLoader的类型为DexPathList的字段Field
                pathList.setAccessible(true);
                Object pathObj = pathList.get(dexClassLoader);//获取pathList的对象

                Class<?> dexPathListClazz = pathObj.getClass();//获取DexPathList这个类的Class
                Field elementsField = dexPathListClazz.getDeclaredField("dexElements");//获取DexPathList的类型为Element[]的field
                elementsField.setAccessible(true);
                Object[] elementObj = (Object[]) elementsField.get(pathObj);//获取DexPathList类的下的dexElements数组，即补丁包的数组


                //加载系统的ClassLoader里面的Element[]对象
                ClassLoader baseClassLoader = base.getClassLoader();
                Class<?> baseClazz = baseClassLoader.getClass();
                Field basePathList = baseClazz.getSuperclass().getDeclaredField("pathList");
                basePathList.setAccessible(true);
                Object basePathListObj = basePathList.get(baseClassLoader);
                Class<?> basePathListClazz = basePathListObj.getClass();
                Field baseElementField = basePathListClazz.getDeclaredField("dexElements");
                baseElementField.setAccessible(true);
                Object[] baseElementObj = (Object[]) baseElementField.get(basePathListObj);//原app的数组

                //合并成一个，并将补丁包的Element数组放在原来的前面
                Object[] array = (Object[]) Array.newInstance(baseElementObj.getClass().getComponentType(), elementObj.length + baseElementObj.length);
                System.arraycopy(elementObj, 0, array, 0, elementObj.length);
                System.arraycopy(baseElementObj, 0, array, elementObj.length, baseElementObj.length);

                //反射将新的数组设置回去
                Field dexElements = basePathListObj.getClass().getDeclaredField("dexElements");
                dexElements.setAccessible(true);
                dexElements.set(basePathListObj, array);
                android.util.Log.i("JApplication", "成功了！");

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                android.util.Log.i("JApplication", "失败！" + e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                android.util.Log.i("JApplication", "失败！" + e.getMessage());
            }

        }
    }
}
