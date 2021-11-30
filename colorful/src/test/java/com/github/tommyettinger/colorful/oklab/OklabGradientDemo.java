package com.github.tommyettinger.colorful.oklab;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;

import static com.badlogic.gdx.Gdx.input;

public class OklabGradientDemo extends ApplicationAdapter {
    public static final int SCREEN_WIDTH = 808;
    public static final int SCREEN_HEIGHT = 600;
    private Stage stage;
    private TextField tf, tf2;
    private TextureRegion pixel;
    private final FloatArray colors = new FloatArray(1024);
    private float start, end;
    private Interpolation interpolation = Interpolation.linear;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Gradient Demo");
        config.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
        config.setIdleFPS(20);
        config.useVsync(true);
        final OklabGradientDemo app = new OklabGradientDemo();
        new Lwjgl3Application(app, config);
    }


    @Override
    public void create() {
        Gdx.graphics.setTitle("Oklab Color Gradients");
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.getFont("default-font").getData().scale(2f);
        pixel = skin.getAtlas().findRegion("white");
        stage = new Stage();
        tf = new TextField("dullest red", skin);
        tf2 = new TextField("light blue", skin);
        start = SimplePalette.parseDescription("dullest red");
        end = SimplePalette.parseDescription("light blue");
        colors.clear();
        GradientTools.appendGradient(colors, start, end, Gdx.graphics.getBackBufferWidth(), interpolation);
        tf.setDisabled(false);
        tf2.setDisabled(false);
        Table tab = new Table(skin);
        tab.align(Align.center);
        tab.setFillParent(true);
        Label title = new Label("Oklab Color Description", skin);
        title.setAlignment(Align.center);
        tab.add(title).growX().minWidth(300).row();
        tab.add(tf).left().growX();
        tab.add(tf2).right().row();
        stage.getRoot().addActor(tab);
        stage.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ESCAPE){
                    Gdx.app.exit();
                    return true;
                }
                return super.keyUp(event, keycode);
            }
        });
        input.setInputProcessor(stage);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float c = SimplePalette.parseDescription(tf.getText());
        boolean changed = colors.size != Gdx.graphics.getBackBufferWidth();
        if(ColorTools.alphaInt(c) >= 254){
            changed |= start != (start = c);
        }
        c = SimplePalette.parseDescription(tf2.getText());
        if(ColorTools.alphaInt(c) >= 24){
            changed |= end != (end = c);
        }
        if(changed){
            colors.clear();
            GradientTools.appendGradient(colors, start, end, Gdx.graphics.getBackBufferWidth(), interpolation);
            System.out.println(colors.size + " " + Gdx.graphics.getBackBufferWidth());
        }
        stage.act();
        Camera camera = stage.getViewport().getCamera();
        camera.update();

        if (!stage.getRoot().isVisible()) return;

        Batch batch = stage.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float width = Gdx.graphics.getBackBufferWidth(), height = Gdx.graphics.getBackBufferHeight();
        for (int i = 0; i < colors.size; i++) {
            batch.setPackedColor(ColorTools.toRGBA(colors.get(i)));
            batch.draw(pixel, i * width / colors.size, 0, width / colors.size, height);
        }
        stage.getRoot().draw(batch, 1);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
