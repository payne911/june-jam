package com.marvelousbob.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.marvelousbob.client.network.MyClient;
import com.marvelousbob.client.network.bound.Msg;
import com.marvelousbob.client.network.bound.Ping;
import com.marvelousbob.client.splashScreen.ISplashWorker;
import lombok.Setter;


public class MarvelousBob extends ApplicationAdapter {

    /* Splash Screen. */
    @Setter
    private ISplashWorker splashWorker;

    /* Display. */
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;
    public Table root;
    public Stage stage;

    /* Client. */
    private MyClient client;

    @Override
    public void create() {
        removeSplashScreen();
        createClient();

        batch = new SpriteBatch();
        initializeScene2d();
    }

    private void initializeScene2d() {
        /* https://github.com/raeleus/skin-composer/wiki/From-the-Ground-Up-00:-Scene2D-Primer */
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        font = new BitmapFont();
        root = new Table(skin);
        root.setDebug(true);
        root.setFillParent(true);
        stage.addActor(root);
        root.defaults().fillX().pad(20);

        pingComponent();
        root.row(); // start next row
        msgComponent();
    }

    private void createClient() {
        System.out.println("\nisRemote? " + Boolean.parseBoolean(System.getenv("mbs_isRemote")));
        this.client = new MyClient(Boolean.parseBoolean(System.getenv("mbs_isRemote")));
        client.connect();
    }

    private void removeSplashScreen() {
        splashWorker.closeSplashScreen();
    }

    private void msgComponent() {
        Table msgComponent = new Table(skin);
        Button btn2 = new TextButton("  > MSG <  ", skin);
        btn2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btn2.setDisabled(true);
                client.getClient().sendTCP(new Msg("Just a test message sent from a button."));
                btn2.setDisabled(false);
            }
        });
        msgComponent.add(btn2);
        root.add(msgComponent);
    }

    private void pingComponent() {
        Table pingComponent = new Table(skin);
        TextArea textArea = new TextArea("placeholder", skin);
        pingComponent.add(textArea).width(300).height(90);
        Button btn = new TextButton("  > PING <  ", skin);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btn.setDisabled(true);
                for (int i = 0; i < 50; i++) {
                    client.getClient().sendTCP(new Ping(System.currentTimeMillis()));
                }
                textArea.setText(client.getLatencyReport()
                        .toString()); // todo: should be in a callback in the client?
                btn.setDisabled(false);
            }
        });
        pingComponent.add(btn);
        root.add(pingComponent);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.2f, .2f, .2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        batch.begin();
//        batch.draw(img, 0, 0);
//        batch.end();

        /* Drawing the UI over the game. */
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // this changes viewed screen size, rather than stretch the view
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
}
