package vac.yufeng.com.jhotfix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.LayoutInflaterCompat;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        try {
//            Field mFactorySet = layoutInflater.getClass().getDeclaredField("mFactorySet");
//            mFactorySet.setAccessible(true);
//
//            mFactorySet.set(layoutInflater, false);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        LayoutInflaterCompat.setFactory2(layoutInflater, new LayoutInflater.Factory2() {
//            @Override
//            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//                return null;
//            }
//
//            @Override
//            public View onCreateView(String name, Context context, AttributeSet attrs) {
//                return null;
//            }
//        });
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "修复了", Toast.LENGTH_LONG).show();
                throw new NullPointerException("崩溃了");
            }
        });

        textView = findViewById(R.id.text);
        findViewById(R.id.skin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();

            }
        });
    }

    private void init() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "skin.apk");
            if (file.exists()) {
                AssetManager assetManager = AssetManager.class.newInstance();
                Method method = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
                method.invoke(assetManager, file.getAbsolutePath());

                Resources resources = new Resources(assetManager, super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());

                int textColor = resources.getColor(R.color.colorAccent);

                textView.setTextColor(textColor);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
