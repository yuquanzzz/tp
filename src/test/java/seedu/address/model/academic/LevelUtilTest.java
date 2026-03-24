package seedu.address.model.academic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LevelUtilTest {

    @Test
    public void levelFromString_validBasic_returnsBasic() {
        assertEquals(Level.BASIC, LevelUtil.levelFromString("basic"));
    }

    @Test
    public void levelFromString_validStrong_returnsStrong() {
        assertEquals(Level.STRONG, LevelUtil.levelFromString("strong"));
    }

    @Test
    public void levelFromString_caseInsensitive_success() {
        assertEquals(Level.BASIC, LevelUtil.levelFromString("BASIC"));
        assertEquals(Level.STRONG, LevelUtil.levelFromString("StRoNg"));
    }

    @Test
    public void levelFromString_trimmedInput_success() {
        assertEquals(Level.BASIC, LevelUtil.levelFromString("  basic  "));
        assertEquals(Level.STRONG, LevelUtil.levelFromString("  strong "));
    }

    @Test
    public void levelFromString_invalidInput_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> LevelUtil.levelFromString("invalid")
        );

        assertThrows(
                IllegalArgumentException.class, () -> LevelUtil.levelFromString("")
        );

        assertThrows(
                IllegalArgumentException.class, () -> LevelUtil.levelFromString("advanced")
        );
    }

    @Test
    public void levelFromString_nullInput_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> LevelUtil.levelFromString(null)
        );
    }
}
