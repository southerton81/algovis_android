package dmitriy.com.algovis;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import com.bowyer.app.fabtoolbar.FabToolbar;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class MainActivity extends AppCompatActivity implements  VisualizationFragment.Listener {

    private DisplayMetrics mDisplayMetrics;
    private boolean mIsVisualOver = false;

    private final String FAB_TB_VISIBILITY_KEY = "FAB_TB_VISIBILITY_KEY";
    private final String FF_BUTON_SELECTED_KEY = "FF_BUTON_SELECTED_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFab(savedInstanceState);
        createFabToolbarButtons(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FabToolbar fabToolbar = (FabToolbar) findViewById(R.id.fabtoolbar);
        ImageButton fforwardButton = (ImageButton) findViewById(R.id.buttonFforward);
        if (fabToolbar != null)
            outState.putBoolean(FAB_TB_VISIBILITY_KEY, fabToolbar.isFabExpanded());
        if (fforwardButton != null)
            outState.putBoolean(FF_BUTON_SELECTED_KEY, fforwardButton.isSelected());
    }

    public void onVisualizationOver() {
        FabToolbar fabToolbar = (FabToolbar) findViewById(R.id.fabtoolbar);
        if (fabToolbar != null && fabToolbar.isFabExpanded())
            fabToolbar.contractFab();
        mIsVisualOver = true;
    }

    private void createFab(Bundle savedInstanceState) {
        final FabToolbar fabToolbar = (FabToolbar) findViewById(R.id.fabtoolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fabToolbar.setFab(fab);

        BitmapDrawable fabIcon = new BitmapDrawable(getResources(),
                Svg2Bitmap(SVGParser.getSVGFromResource(getResources(), R.raw.play48), fabIconSize()));
        fab.setImageDrawable(fabIcon);

        fabToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisualizationFragment fragment = (VisualizationFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mainActivityFragment);
                if (!fabToolbar.isFabExpanded() && fragment != null) {
                    fabToolbar.expandFab();
                    if (mIsVisualOver)
                        fragment.resetVisual();
                    else
                        fragment.startVisual();
                    mIsVisualOver = false;
                }
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(FAB_TB_VISIBILITY_KEY, false)) {
                fabToolbar.show();
                fab.hide();
            }
        }
    }

    private void createFabToolbarButtons(Bundle savedInstanceState) {
        BitmapDrawable reset = new BitmapDrawable(getResources(),
                Svg2Bitmap(SVGParser.getSVGFromResource(getResources(), R.raw.replay4), smallIconSize()));
        BitmapDrawable pause = new BitmapDrawable(getResources(),
                Svg2Bitmap(SVGParser.getSVGFromResource(getResources(), R.raw.pause44), smallIconSize()));
        BitmapDrawable fast = new BitmapDrawable(getResources(),
                Svg2Bitmap(SVGParser.getSVGFromResource(getResources(), R.raw.fast19), smallIconSize()));

        ImageButton buttonReset = (ImageButton) findViewById(R.id.buttonReset);
        ImageButton buttonPause = (ImageButton) findViewById(R.id.buttonPause);
        final ImageButton buttonFaster = (ImageButton) findViewById(R.id.buttonFforward);

        buttonReset.setImageDrawable(reset);
        buttonPause.setImageDrawable(pause);
        buttonFaster.setImageDrawable(fast);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisualizationFragment fragment = (VisualizationFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mainActivityFragment);
                if (fragment != null) {
                    fragment.resetVisual();
                }
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisualizationFragment fragment = (VisualizationFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mainActivityFragment);
                FabToolbar fabToolbar = (FabToolbar) findViewById(R.id.fabtoolbar);
                if (fragment != null && fabToolbar != null) {
                    fragment.pauseVisual();
                    fabToolbar.contractFab();
                }
            }
        });

        buttonFaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisualizationFragment fragment = (VisualizationFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mainActivityFragment);
                if (fragment != null)
                    buttonFaster.setSelected(!fragment.fastForwardVisual());
            }
        });

        if (savedInstanceState != null) {
            buttonFaster.setSelected(savedInstanceState.getBoolean(FF_BUTON_SELECTED_KEY, false));
        }
    }

    private static Bitmap Svg2Bitmap(SVG svg, float size) {
        RectF rect = svg.getLimits();
        float w = rect.right + rect.left;
        float h = rect.bottom + rect.top;

        float scale;
        if (w > h) scale = size / w;
        else scale = size / h;
        w *= scale;
        h *= scale;
        Bitmap bitmap = Bitmap.createBitmap((int)w, (int)h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(scale, scale);
        canvas.drawPicture(svg.getPicture());
        return bitmap;
    }

    private float fabIconSize() {
        // https://www.google.com/design/spec/components/buttons-floating-action-button.html#buttons-floating-action-button-floating-action-button
        return Math.round(24 * density());
    }

    public int smallIconSize() {
        return Math.round(20 * density());
    }

    private float density() {
        if (mDisplayMetrics == null) {
            mDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        return mDisplayMetrics.density;
    }
}
