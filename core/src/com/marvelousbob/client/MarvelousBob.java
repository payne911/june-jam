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

public class MarvelousBob extends ApplicationAdapter {

    /* Loading. */
    private ISplashWorker splashWorker;
    private boolean finishedLoading = false;

    /* Display. */
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;
    public Table root;
    public Stage stage;

    /* Client. */
    private MyClient client =
            new MyClient(Boolean.parseBoolean(System.getenv("mbs_isRemote")));

    @Override
    public void create() {
        System.out.println("\nisRemote? " + Boolean.parseBoolean(System.getenv("mbs_isRemote")));
        splashWorker.closeSplashScreen();
        client.connect();

        batch = new SpriteBatch();

        System.out.println("client create");
        batch = new SpriteBatch();
        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        /* https://github.com/raeleus/skin-composer/wiki/From-the-Ground-Up-00:-Scene2D-Primer */
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        root = new Table(skin);
        root.setDebug(true);
        root.setFillParent(true);
        stage.addActor(root);
        root.defaults().fillX().pad(20);

        Table pingComponent = new Table(skin);
        TextArea textArea = new TextArea("placeholder", skin);
        pingComponent.add(textArea).width(300).height(90);
        Button btn = new TextButton("  > PING <  ", skin);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btn.setDisabled(true);
                for (int i = 0; i < 15000; i++) {
                    client.getClient().sendTCP(new Ping(System.currentTimeMillis()));
                    textArea.setText(client.getLatencyReport().toString());
                }
                btn.setDisabled(false);
            }
        });
        pingComponent.add(btn);
        root.add(pingComponent);

        root.row(); // start next row
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


    public ISplashWorker getSplashWorker() {
        return splashWorker;
    }

    public void setSplashWorker(ISplashWorker splashWorker) {
        this.splashWorker = splashWorker;
    }
}
