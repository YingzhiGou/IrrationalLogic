package im.irrational.logic.propositional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LiteralTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getValue() {
        Literal literal = new Literal("test", 1);
        assertEquals(1, literal.getValue());
    }

    @Test
    void getName() {
        Literal literal = new Literal("test", 1);
        assertEquals("test", literal.getName());
    }

    @Test
    void test_toString() {
        Literal literal = new Literal("test", 1);
        assertEquals("test", literal.toString());
        assertEquals(Literal.NEGATION_SYMBOL + "test", literal.negation().toString());
    }

    @Test
    void negation() {
        Literal literal = new Literal("test", 1);
        literal = literal.negation();
        assertEquals(-1, literal.getValue());
        assertEquals("test", literal.getName());
        assertEquals(Literal.NEGATION_SYMBOL + "test", literal.toString());
        literal = literal.negation();
        assertEquals(1, literal.getValue());
        assertEquals("test", literal.getName());
        assertEquals("test", literal.toString());
    }

    @Test
    void test_clone() {
        Literal literal = new Literal("test", 1);
        Literal cloned = literal.clone();
        literal = literal.negation();
        assertNotEquals(literal, cloned);
        assertEquals(literal.getName(), cloned.getName());
        assertNotEquals(literal.getValue(), cloned.getValue());
        cloned = cloned.negation();
        assertEquals(literal, cloned);
        assertEquals(literal.getName(), cloned.getName());
        assertEquals(literal.getValue(), cloned.getValue());
    }

    @Test
    void equals() {
        Literal literal = new Literal("test", 1);
        assertTrue(literal.equals(literal));
        assertTrue(literal.equals(literal.clone()));
        assertFalse(literal.equals(literal.negation()));
        Literal other = new Literal("anotherTest", 1);
        assertFalse(literal.equals(other));
        assertFalse(literal.equals(other.negation()));
    }

    @Test
    void test_hashCode() {
        Literal literal = new Literal("test", 1);
        assertEquals(literal.hashCode(), literal.clone().hashCode());
        assertNotEquals(literal.hashCode(), literal.negation().hashCode());
        Literal other = new Literal("test1", 1);
        assertNotEquals(literal.hashCode(), other.hashCode());
    }
}