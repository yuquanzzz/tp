package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CommandAutocompleteTest {

    private final CommandAutocomplete autocomplete = new CommandAutocomplete();

    @Test
    public void evaluate_emptyInput_showsNoSuggestions() {
        CommandAutocomplete.AutocompleteResult result = autocomplete.evaluate("");

        assertTrue(result.getSuggestions().isEmpty());
    }

    @Test
    public void evaluate_partialTopLevelCommand_matchesAndCompletes() {
        CommandAutocomplete.AutocompleteResult result = autocomplete.evaluate("ed");
        List<CommandAutocomplete.Suggestion> suggestions = result.getSuggestions();

        assertFalse(suggestions.isEmpty());
        assertEquals("edit", suggestions.get(0).getValue());
        assertEquals("edit ", suggestions.get(0).applyTo("ed"));
    }

    @Test
    public void evaluate_subcommandInput_matchesAndCompletes() {
        CommandAutocomplete.AutocompleteResult result = autocomplete.evaluate("edit st");
        List<CommandAutocomplete.Suggestion> suggestions = result.getSuggestions();

        assertFalse(suggestions.isEmpty());
        assertEquals("student", suggestions.get(0).getValue());
        assertEquals("edit student ", suggestions.get(0).applyTo("edit st"));
    }

    @Test
    public void evaluate_commandWithSubcommands_showsSubcommandHint() {
        CommandAutocomplete.AutocompleteResult result = autocomplete.evaluate("find");

        assertTrue(result.getHint().startsWith("Subcommands:"));
    }

    @Test
    public void evaluate_commandSelection_showsFieldHint() {
        CommandAutocomplete.AutocompleteResult result = autocomplete.evaluate("add ");

        assertTrue(result.getHint().contains("Required: n/ p/ e/ a/"));
        assertTrue(result.getHint().contains("Optional: t/"));
    }

    @Test
    public void evaluate_subcommandSelection_showsFieldHint() {
        CommandAutocomplete.AutocompleteResult result = autocomplete.evaluate("edit student ");

        assertTrue(result.getHint().contains("Required: INDEX"));
        assertTrue(result.getHint().contains("Optional: n/ p/ e/ a/"));
    }
}
