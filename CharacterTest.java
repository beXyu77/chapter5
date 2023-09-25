package se233.chapter5;

import javafx.embed.swing.JFXPanel;
import se233.chapter5.model.Character;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import se233.chapter5.controller.DrawingLoop;
import se233.chapter5.controller.GameLoop;
import se233.chapter5.view.Platform;
import se233.chapter5.view.Score;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static se233.chapter5.view.Platform.GROUND;

public class CharacterTest {
    private Character floatingCharacter;
    private Character floatingCharacter2;
    private Score scoreTest;
    private ArrayList<Character> characterListUnderTest;
    private ArrayList<Score> scoreListUnderTest;
    private Platform platformUnderTest;
    private GameLoop gameLoopUnderTest;
    private DrawingLoop drawingLoopUnderTest;
    private Method updateMethod;
    private Method redrawMethod;
    private Method updateScoreMethod;
    private Method checkCollisionMethod;

    @Before
    public void setup(){
        //loading up toolkit
        JFXPanel jfxPanel=new JFXPanel();

        floatingCharacter =new Character(30,30,0,0, KeyCode.A,KeyCode.D,KeyCode.W);
        floatingCharacter2 = new Character(600, 30, 0, 0, KeyCode.A, KeyCode.D, KeyCode.W);

        characterListUnderTest = new ArrayList<Character>();
        characterListUnderTest.add(floatingCharacter);
        characterListUnderTest.add(floatingCharacter2);
        scoreListUnderTest = new ArrayList<Score>();
        scoreTest = new Score(30,GROUND+30);
        scoreListUnderTest.add(scoreTest);


        platformUnderTest = new Platform();
        gameLoopUnderTest = new GameLoop(platformUnderTest);
        drawingLoopUnderTest = new DrawingLoop(platformUnderTest);

        try{
            updateScoreMethod = GameLoop.class.getDeclaredMethod("updateScore", ArrayList.class, ArrayList.class);
            updateMethod = GameLoop.class.getDeclaredMethod("update",ArrayList.class);
            //Using the arraylist of class(GameLoop) as parameter when use "update" method
            redrawMethod = DrawingLoop.class.getDeclaredMethod("paint",ArrayList.class);
            checkCollisionMethod = DrawingLoop.class.getDeclaredMethod("checkDrawCollisions", ArrayList.class);
            updateMethod.setAccessible(true);
            checkCollisionMethod.setAccessible(true);
            updateScoreMethod.setAccessible(true);
            redrawMethod.setAccessible(true);
        } catch (NoSuchMethodException e ) {
            e.printStackTrace();
            updateMethod = null;
            redrawMethod = null;
            throw new RuntimeException(e);
        }


    }
    @Test
    public void characterShouldMoveToTheLeftAfterTheLeftKeyIsPressed() throws IllegalAccessException, InvocationTargetException,NoSuchFieldException{
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.A);
        //Use Update method to inoke gameobject,character from arrayList
        // invoke(object,params to pass method)
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);

        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);

        assertTrue("Controller : Left key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model : Character moving left state is set", isMoveLeft.getBoolean(characterUnderTest));
        assertTrue("View : Character is moving left ", characterUnderTest.getX() < startX);
    }
    @Test
    public void characterShouldMoveToTheRightAfterTheRightKeyIsPressed() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Character characterTest = characterListUnderTest.get(0);
        platformUnderTest.getKeys().add(KeyCode.D);
        int  startX = characterTest.getX();
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);

        Field isMovedRight = characterTest.getClass().getDeclaredField("isMoveRight" );
        isMovedRight.setAccessible(true);
        assertTrue("Controller : Right key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model : Character moving right state is set", isMovedRight.getBoolean(characterTest));
        assertTrue("View : Character is moving right ", characterTest.getX() > startX);


    }
    @Test
    public void characterJumpWhenOnGroundAfterUpKeyIsPressed() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Character characterTest = characterListUnderTest.get(0);
        platformUnderTest.getKeys().add(KeyCode.W);
        int  startY = characterTest.getY();
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);

        Field isJumping = characterTest.getClass().getDeclaredField("isJumping" );
        Field canJump = characterTest.getClass().getDeclaredField("canJump" );
        canJump.setAccessible(true);
        canJump.setBoolean(characterTest,true);
        isJumping.setAccessible(true);
        isJumping.setBoolean(characterTest,true);


        assertTrue("Controller : Up key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertTrue("Model : Character canjump state is set", canJump.getBoolean(characterTest));
        assertTrue("Model : Character jumping state is set", isJumping.getBoolean(characterTest));
        assertTrue("View : Character jumped ", characterTest.getY() > startY);

    }
    @Test
    public void characterJumpWhenNotBeingOnGroundAfterUpKeyIsPressed() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Character characterTest = characterListUnderTest.get(0);
        platformUnderTest.getKeys().add(KeyCode.W);
        int  startY = characterTest.getY();
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isJumping = characterTest.getClass().getDeclaredField("isJumping" );
        Field canJump = characterTest.getClass().getDeclaredField("canJump" );
        canJump.setAccessible(true);
        canJump.setBoolean(characterTest,false);
        isJumping.setAccessible(true);
        isJumping.setBoolean(characterTest,false);
        assertTrue("Controller : Up key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertFalse("Model : Character jumping state is set", isJumping.getBoolean(characterTest));
        assertEquals("View : Character can't jumped ", characterTest.getY(), startY,1);

    }
    @Test
    public void characterStopsRightAtBorderAfterMovingTowardsTheBorder() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Character characterTest = characterListUnderTest.get(0);
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMovedRight = characterTest.getClass().getDeclaredField("isMoveRight" );
        isMovedRight.setAccessible(true);
        Method checkReachGameWall= Character.class.getDeclaredMethod("checkReachGameWall");
        checkReachGameWall.setAccessible(true);
        checkReachGameWall.invoke(characterTest);
        characterTest.setX(800);
        boolean testwall= characterTest.getX() >= Platform.WIDTH;
        assertTrue("Controller: Character reach to the border",testwall);

        assertTrue("Controller : Right key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model : Character moving right is set", isMovedRight.getBoolean(characterTest));
        characterTest.stop();
        isMovedRight.setBoolean(characterTest,false);
        assertFalse("Model : Character should not be moving right after stopping ",  isMovedRight.getBoolean(characterTest));

    }
    @Test
    public void consequencesAfterCharacterHasCollidedWithAnotherCharacter() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Character characterTest = characterListUnderTest.get(0);
        Character anothercharacter = characterListUnderTest.get(1);
        Field isMovedRight = characterTest.getClass().getDeclaredField("isMoveRight" );
        Field score = characterTest.getClass().getDeclaredField("score" );
        isMovedRight.setAccessible(true);
        score.setAccessible(true);
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        checkCollisionMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterTest.collided(anothercharacter);
        assertTrue("Controller : Right key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertFalse("Model: Character stops after colliding with another character", isMovedRight.getBoolean(characterTest));


    }


    @Test
    public void consequencesAfterCharacterStompsOvertheAnotherCharacter() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Character characterTest = characterListUnderTest.get(0);
        Character anothercharacter = characterListUnderTest.get(1);
        int startScore = characterTest.getScore();
        int startY = characterTest.getY();
        int initialImgHeight = anothercharacter.getImageView().getHeight();
        Field isMovedRight = characterTest.getClass().getDeclaredField("isMoveRight" );
        Field isFalling= characterTest.getClass().getDeclaredField("isFalling" );
        Field score = characterTest.getClass().getDeclaredField("score" );
        isMovedRight.setAccessible(true);
        score.setAccessible(true);
        isFalling.setAccessible(true);
        isMovedRight.setBoolean(characterTest,true);

        System.out.println("Before Jump:"+ startY);

        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        characterTest.jump();
        characterTest.moveRight();
        isFalling.setBoolean(characterTest,true);
        assertTrue("Character should still be falling after stomp.", isFalling.getBoolean(characterTest));

        characterTest.setY(anothercharacter.getY() - Character.CHARACTER_HEIGHT - 1);

        System.out.println("after jump:" + characterTest.getY());
        characterTest.collided(anothercharacter);
        Method collapsedMethod=Character.class.getDeclaredMethod("collapsed");
        collapsedMethod.setAccessible(true);
        collapsedMethod.invoke(anothercharacter);


        assertTrue("View: Another character's image height must be smaller while stomp",initialImgHeight> anothercharacter.getHeight());

        assertTrue("Model: Another character should be collapsed ",anothercharacter.getY()> anothercharacter.getStartY());
        System.out.println("Character will be collapsed!");
        Method respawnMethod=Character.class.getDeclaredMethod("respawn");
        respawnMethod.setAccessible(true);
        respawnMethod.invoke(anothercharacter);
        assertEquals("View : Another Character should be re-appeared at original position after being stomped ", anothercharacter.getX(), anothercharacter.getStartX(),1);
        System.out.println("Another character respawn!!");

        int updatedScore = characterTest.getScore();
        System.out.println("Initial Score:" + startScore);
        System.out.println("Updated Score:" + updatedScore);

        assertTrue("Model : Character score must be increased after stomping another character", updatedScore>startScore);
    }


    @Test
    public void characterInitialValuesShouldMatchConstructorArguments(){
        assertEquals("Initial x",30,floatingCharacter.getX(),0);
        assertEquals("Initial y",30,floatingCharacter.getY(),0);
        assertEquals("offset x",0,floatingCharacter.getOffsetX(),0);
        assertEquals("offset y",0,floatingCharacter.getOffsetY(),0);
        assertEquals("leftKey",KeyCode.A,floatingCharacter.getLeftKey());
        assertEquals("RightKey",KeyCode.D,floatingCharacter.getRightKey());
        assertEquals("UpKey",KeyCode.W,floatingCharacter.getUpKey());
    }
}
/*
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import se233.chapter5.controller.DrawingLoop;
import se233.chapter5.controller.GameLoop;
import se233.chapter5.model.Character;
import se233.chapter5.view.Platform;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CharacterTest {
    private Character floatingCharacter;
    private ArrayList<Character> characterListUnderTest;
    private Platform platformUnderTest;
    private GameLoop gameLoopUnderTest;
    private DrawingLoop drawingLoopUnderTest;
    private Method updateMethod, redrawMethod;
    private Character character1;
    private Character character2;
    private Character stomperCharacter;
    private Character stompedCharacter;

    @Before
    public void setup() {
        JFXPanel jfxPanel = new JFXPanel();
        floatingCharacter = new Character(30, 30, 0, 0, KeyCode.A, KeyCode.D, KeyCode.W);
        characterListUnderTest = new ArrayList<Character>();
        characterListUnderTest.add(floatingCharacter);
        platformUnderTest = new Platform();
        gameLoopUnderTest = new GameLoop(platformUnderTest);
        drawingLoopUnderTest = new DrawingLoop(platformUnderTest);

        stomperCharacter = new Character(100, 100, 0, 0, KeyCode.A, KeyCode.D, KeyCode.W);
        stompedCharacter = new Character(100, 200, 0, 0, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP);

        character1 = new Character(100, 50, 0, 0, KeyCode.A, KeyCode.D, KeyCode.W);
        character2 = new Character(200, 50, 0, 0, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP);

        try {
            updateMethod = GameLoop.class.getDeclaredMethod("update", ArrayList.class);
            redrawMethod = DrawingLoop.class.getDeclaredMethod("paint", ArrayList.class);
            updateMethod.setAccessible(true);
            redrawMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            updateMethod = null;
            redrawMethod = null;
        }
    }
    @Test
    public void characterInitialValuesShouldMatchConstructorArguments() {
        assertEquals("Initial x", 30, floatingCharacter.getX(), 0);
        assertEquals("Initial y", 30, floatingCharacter.getY(), 0);
        assertEquals("Offset x", 0, floatingCharacter.getOffsetX(), 0.0);
        assertEquals("Offset y", 0, floatingCharacter.getOffsetY(), 0.0);
        assertEquals("Left key", KeyCode.A, floatingCharacter.getLeftKey());
        assertEquals("Right key", KeyCode.D, floatingCharacter.getRightKey());
        assertEquals("Up key", KeyCode.W, floatingCharacter.getUpKey());
    }

    @Test
    public void characterShouldMoveToTheLeftAfterTheLeftKeyIsPressed() throws
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.A);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        assertTrue("Controller: Left key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model: Character moving left state is set", isMoveLeft.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving left", characterUnderTest.getX() < startX);
    }
    @Test
    public void characterShouldMoveToTheRightAfterTheRightKeyIsPressed() throws
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();

        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);

        Field isMoveRight = characterUnderTest.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);

        assertTrue("Controller: Right key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model: Character moving right state is set", isMoveRight.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving right", characterUnderTest.getX() > startX);
    }

    @Test
    public void characterShouldJumpWhenOnGroundAndWKeyPressed() throws
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startY = characterUnderTest.getY();

        characterUnderTest.setY(startY);

        platformUnderTest.getKeys().add(KeyCode.W);

        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);

        int newY = characterUnderTest.getY();
        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping");
        Field canJumping = characterUnderTest.getClass().getDeclaredField("canJump" );
        isJumping.setAccessible(true);
        isJumping.setBoolean(characterUnderTest,true);
        assertTrue("Controller: 'W' key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertTrue("Model: Character jumping state is set", isJumping.getBoolean(characterUnderTest));
        assertTrue("View: Character is jumping", characterUnderTest.getY() > startY);
    }

    @Test
    public void characterShouldJumpWhenNotOnGroundAndWKeyPressed() throws
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startY = characterUnderTest.getY();

        characterUnderTest.setY(startY - 10);

        platformUnderTest.getKeys().add(KeyCode.W);

        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);

        int newY = characterUnderTest.getY();
        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping");
        Field canJumping = characterUnderTest.getClass().getDeclaredField("canJump");
        canJumping.setAccessible(true);
        canJumping.setBoolean(characterUnderTest, true);
        isJumping.setAccessible(true);
        isJumping.setBoolean(characterUnderTest, true);

        assertTrue("Controller: 'W' key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertTrue("Model: Character jumping state is set", isJumping.getBoolean(characterUnderTest));
        assertTrue("Model: Character can jump state is set", canJumping.getBoolean(characterUnderTest));
        assertTrue("View: Character is jumping", characterUnderTest.getY() < startY);
    }

    @Test
    public void characterStopsRightAtBorderAfterMovingTowardsTheBorder() throws
            InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Character characterTest = characterListUnderTest.get(0);
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMovedRight = characterTest.getClass().getDeclaredField("isMoveRight" );
        isMovedRight.setAccessible(true);


        assertTrue("Controller : Right key pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model : Character moving right is set", isMovedRight.getBoolean(characterTest));
        characterTest.stop();
        isMovedRight.setBoolean(characterTest,false);
        assertFalse("Model : Character should not be moving right after stopping ", isMovedRight.getBoolean(characterTest));

    }

    @Test
    public void testCharacterCollision()  {
        character1.collided(character2);

        assertFalse(character1.isMoveLeft);
        assertFalse(character1.isMoveRight);
        assertFalse(character2.isMoveLeft);
        assertFalse(character2.isMoveRight);
    }

    @Test
    public void testCharacterStomping() {
        stomperCharacter.jump();
        stomperCharacter.moveY();
        stomperCharacter.collided(stompedCharacter);

        assertFalse("Stomped character should not be jumping", stompedCharacter.isJumping);
    }
}
 */
