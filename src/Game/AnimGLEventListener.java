package Game;

import Texture.TextureReader;
import Texture.AnimListener;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import java.util.ArrayList;
import java.util.Random;

public class AnimGLEventListener extends AnimListener implements MouseListener, MouseMotionListener {
    ArrayList<Letter> letters = new ArrayList<>();
    ArrayList<Card> cards = new ArrayList<>();
    int[] win = new int[]{28, 18, 24, 26, 12, 17};
    int score = 0;
    int maxWidth = 100;
    int maxHeight = 100;
    int alphabetX = 0;
    int alphabetY = 0;
    int removedCard;
    private Card incorrectCard = null;
    private boolean secondPressed = false;
    private int returnCounter = 0;
    int removedCardPos = -1;
    String alphabetName = "";
    double mouseX;
    double mouseY;

    public AnimGLEventListener() {
    }

    String[] textureNames = {
            "Man1.png", "Man2.png", "Man3.png", "Man4.png",
            "a.png", "b.png", "c.png", "d.png", "e.png", "f.png", "g.png", "h.png", "i.png", "j.png", "k.png", "l.png", "m.png", "n.png", "o.png", "p.png", "q.png", "r.png", "s.png", "t.png", "u.png", "v.png", "w.png", "x.png", "y.png", "z.png",
            "0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "9.png", "..png",
            "HealthB.png", "Health.png",
            "Back.png",
            "NinjaStar.png"
    };
    TextureReader.Texture[] texture = new TextureReader.Texture[textureNames.length];
    public int[] textures = new int[textureNames.length];

    public void init(GLAutoDrawable gld) {
        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA,
                        texture[i].getWidth(),
                        texture[i].getHeight(),
                        GL.GL_RGBA,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels()
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        for (int i = 0; i <= 'z' - 'a'; i++) {
            letters.add(new Letter(alphabetX, alphabetY, i + 4, 1 , 1));
            letters.add(new Letter(alphabetX, alphabetY, i + 4, 1 , 1));
            cards.add(new Card(alphabetX, alphabetY, textureNames.length - 3, 1.09f , 1));
            cards.add(new Card(alphabetX, alphabetY, textureNames.length - 3, 1.09f , 1));
        }
        shuffleLetters();
        drawLetters();
        drawImageGrid();
    }

    @Override
    public void display(GLAutoDrawable gld) {
        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        DrawBackground(gl);
        if(secondPressed) returnCounter++;
        if(returnCounter >= 50
        ){
            incorrectCard.setY(incorrectCard.getY()-1000);
            cards.get(removedCard).setY(cards.get(removedCard).getY()-1000);
            secondPressed = false;
            returnCounter=0;
        }
        for (Letter letter : letters) letter.drawSprite(gl, textures);
        for (Card card : cards) card.drawSprite(gl, textures);
        if (score == cards.size() / 2)
            for (int i = 0; i < win.length; i++) {
                if (i < 3) DrawSprite(gl, 2 + (15 * i), 50, win[i], 1.5f, 3f);
                else DrawSprite(gl, 12 + (15 * i), 50, win[i], 1.5f, 3f);
            }

    }

    public void drawImageGrid() {
        int xPos = 0;
        int yPos = 90;
        for (Card card : cards) {
            card.setX(xPos);
            card.setY(yPos);
            xPos += 13;
            if (xPos >= maxWidth) {
                xPos = 0;
                yPos -= 12;
            }
        }
    }

    public void drawLetters() {
        int xPos = 0;
        int yPos = 90;
        for (Letter letter : letters) {
            letter.setX(xPos);
            letter.setY(yPos);
            xPos += 13;
            if (xPos >= maxWidth) {
                xPos = 0;
                yPos -= 12;
            }
        }
    }

    public void shuffleLetters() {
        Random rand = new Random();
        for (int i = letters.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = letters.get(i).getTextureIndex();
            letters.get(i).setTextureIndex(letters.get(j).getTextureIndex());
            letters.get(j).setTextureIndex(temp);
        }
    }

    public void removeCard() {
        removedCardPos = -1;
        secondPressed = false;
        returnCounter =0;
        for (int i = 0; i < cards.size(); i++) {
            returnCounter = 0;
            incorrectCard = null;
            Card l = cards.get(i);
            if (mouseX <= (int) l.getX() + 10 && mouseX >= l.getX() && mouseY <= (int) l.getY() + 10 && mouseY >= l.getY() - 1) {
                alphabetName = textureNames[letters.get(i).getTextureIndex()];
                removedCardPos = (int) l.getY();
                l.setY(l.getY()+1000);
                removedCard = i;
                break;
            }
        }
    }

    public void check() {
        if (returnCounter == 0) {
            if (removedCardPos == -1) removeCard();
            else {
                for (int i = 0; i < cards.size(); i++) {
                    Card card = cards.get(i);
                    if (mouseX <= (int) card.getX() + 10 && mouseX >= card.getX() && mouseY <= (int) card.getY() + 10 && mouseY >= card.getY() - 1) {
                        if (alphabetName.equals(textureNames[letters.get(i).getTextureIndex()])) {
                            card.setY(1000);
                            score++;
                        } else {
                            incorrectCard = card;
                            card.setY(card.getY() + 1000);
                            secondPressed = true;
                        }
                        returnCounter = 0;
                        removedCardPos = -1;
                        break;
                    }
                }
            }
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        mouseX = (int) convertX(e.getX(), e.getComponent().getWidth(), 0, 100);
        mouseY = (int) convertY(e.getY(), e.getComponent().getHeight(), 0, 100);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        check();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public double convertX(double x, double screenWidth, double left, double right) {
        return left + (x / screenWidth) * (right - left);
    }

    public double convertY(double y, double screenHeight, double bottom, double top) {
        return top - (y / screenHeight) * (top - bottom);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void DrawSprite(GL gl, float x, float y, int index, float scaleX, float scaleY) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);

        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.1 * scaleX, 0.1 * scaleY, 1);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length - 2]);

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}


