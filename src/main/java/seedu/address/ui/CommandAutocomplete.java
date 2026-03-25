package seedu.address.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeletePersonCommand;
import seedu.address.logic.commands.EditAcademicsCommand;
import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.commands.EditAttdCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditParentCommand;
import seedu.address.logic.commands.EditPaymentCommand;
import seedu.address.logic.commands.EditPersonCommand;
import seedu.address.logic.commands.EditTagCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindPersonCommand;
import seedu.address.logic.commands.FindTagCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewApptCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.CliSyntax;

/**
 * Computes shell-like command suggestions and field hints for the command box.
 */
public class CommandAutocomplete {

    private final Map<String, CommandSpec> commandSpecs;
    private final List<String> topLevelCommands;

    /**
     * Creates the autocomplete engine with command metadata used for suggestions and hints.
     */
    public CommandAutocomplete() {
        this.commandSpecs = createCommandSpecs();
        this.topLevelCommands = new ArrayList<>(commandSpecs.keySet());
    }

    /**
     * Builds suggestions and hint text from the current command input.
     */
    public AutocompleteResult evaluate(String inputText) {
        String rawInput = inputText == null ? "" : inputText;
        TokenState tokenState = TokenState.from(rawInput);

        if (tokenState.tokens.isEmpty()) {
            return new AutocompleteResult(Collections.emptyList(), "");
        }

        String normalizedCommand = tokenState.tokens.get(0).toLowerCase(Locale.ROOT);
        CommandSpec commandSpec = commandSpecs.get(normalizedCommand);

        if (tokenState.tokens.size() == 1 && !tokenState.endsWithWhitespace) {
            return new AutocompleteResult(
                    toSuggestions(topLevelCommands, tokenState.tokens.get(0), SuggestionMode.COMMAND),
                    buildCommandHint(commandSpec));
        }

        if (commandSpec == null) {
            return new AutocompleteResult(Collections.emptyList(), "");
        }

        if (commandSpec.subcommands.isEmpty()) {
            return new AutocompleteResult(Collections.emptyList(), buildFieldHint(commandSpec.fieldSpec));
        }

        if (tokenState.tokens.size() == 1 && tokenState.endsWithWhitespace) {
            return new AutocompleteResult(
                    toSuggestions(commandSpec.subcommands.keySet(), "", SuggestionMode.SUBCOMMAND),
                    buildSubcommandHint(commandSpec));
        }

        String subcommandToken = tokenState.tokens.get(1);
        String normalizedSubcommand = subcommandToken.toLowerCase(Locale.ROOT);
        FieldSpec subcommandFieldSpec = commandSpec.subcommands.get(normalizedSubcommand);

        if (tokenState.tokens.size() == 2 && !tokenState.endsWithWhitespace) {
            return new AutocompleteResult(
                    toSuggestions(commandSpec.subcommands.keySet(), subcommandToken, SuggestionMode.SUBCOMMAND),
                    subcommandFieldSpec == null
                            ? buildSubcommandHint(commandSpec)
                            : buildFieldHint(subcommandFieldSpec));
        }

        if (subcommandFieldSpec == null) {
            return new AutocompleteResult(Collections.emptyList(), buildSubcommandHint(commandSpec));
        }

        return new AutocompleteResult(Collections.emptyList(), buildFieldHint(subcommandFieldSpec));
    }

    private String buildCommandHint(CommandSpec commandSpec) {
        if (commandSpec == null) {
            return "";
        }
        if (!commandSpec.subcommands.isEmpty()) {
            return buildSubcommandHint(commandSpec);
        }
        return buildFieldHint(commandSpec.fieldSpec);
    }

    private String buildSubcommandHint(CommandSpec commandSpec) {
        String subcommands = String.join(", ", commandSpec.subcommands.keySet());
        return "Subcommands: " + subcommands;
    }

    private String buildFieldHint(FieldSpec fieldSpec) {
        List<String> parts = new ArrayList<>();
        if (!fieldSpec.required.isEmpty()) {
            parts.add("Required: " + String.join(" ", fieldSpec.required));
        }
        if (!fieldSpec.optional.isEmpty()) {
            parts.add("Optional: " + String.join(" ", fieldSpec.optional));
        }
        if (parts.isEmpty()) {
            return "No fields needed";
        }
        return String.join(" | ", parts);
    }

    private List<Suggestion> toSuggestions(Iterable<String> candidates, String token, SuggestionMode mode) {
        String normalizedToken = token.toLowerCase(Locale.ROOT);
        List<SuggestionMatch> matches = new ArrayList<>();

        for (String candidate : candidates) {
            String normalizedCandidate = candidate.toLowerCase(Locale.ROOT);
            int prefixIndex = normalizedCandidate.indexOf(normalizedToken);
            if (normalizedToken.isEmpty()) {
                matches.add(new SuggestionMatch(candidate, 0));
                continue;
            }
            if (prefixIndex == 0) {
                matches.add(new SuggestionMatch(candidate, 0));
            } else if (normalizedCandidate.contains(normalizedToken)) {
                matches.add(new SuggestionMatch(candidate, 1));
            }
        }

        matches.sort(Comparator
                .comparingInt((SuggestionMatch match) -> match.rank)
                .thenComparing(match -> match.value));

        return matches.stream()
                .map(match -> new Suggestion(match.value, mode))
                .collect(Collectors.toList());
    }

    private static Map<String, CommandSpec> createCommandSpecs() {
        Map<String, CommandSpec> specs = new LinkedHashMap<>();

        specs.put(AddCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(
                listOf("n/", "p/", "e/", "a/"),
                listOf(CliSyntax.PREFIX_TAG.toString()))));

        specs.put(EditCommand.COMMAND_WORD, CommandSpec.withSubcommands(mapOf(
                EditPersonCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX"), listOf("n/", "p/", "e/", "a/")),
                EditTagCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX"), listOf(CliSyntax.PREFIX_TAG.toString())),
                EditAcademicsCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX"), listOf("s/", "l/")),
                EditParentCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX"), listOf("pn/", "pp/", "pe/")),
                EditApptCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX", "d/"), listOf()),
                EditPaymentCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX", "d/"), listOf()),
                EditAttdCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX"), listOf("d/")))));

        specs.put(DeleteCommand.COMMAND_WORD, CommandSpec.withSubcommands(mapOf(
                DeletePersonCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("INDEX"), listOf()))));

        specs.put(ClearCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(listOf(), listOf())));

        specs.put(FindCommand.COMMAND_WORD, CommandSpec.withSubcommands(mapOf(
                FindPersonCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("KEYWORD"), listOf("[MORE_KEYWORDS]")),
                FindTagCommand.SUB_COMMAND_WORD,
                FieldSpec.of(listOf("t/TAG"), listOf("t/MORE_TAGS")))));

        specs.put(ListCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(listOf(), listOf())));
        specs.put(ViewCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(listOf("INDEX"), listOf())));
        specs.put(ViewApptCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(listOf(), listOf("d/"))));
        specs.put(ExitCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(listOf(), listOf())));
        specs.put(HelpCommand.COMMAND_WORD, CommandSpec.of(FieldSpec.of(listOf(), listOf())));

        return specs;
    }

    private static List<String> listOf(String... values) {
        return Arrays.asList(values);
    }

    private static Map<String, FieldSpec> mapOf(Object... values) {
        Map<String, FieldSpec> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            String key = ((String) values[i]).toLowerCase(Locale.ROOT);
            FieldSpec value = (FieldSpec) values[i + 1];
            map.put(key, value);
        }
        return map;
    }

    /**
     * Result for command autocomplete evaluation.
     */
    public static class AutocompleteResult {
        private final List<Suggestion> suggestions;
        private final String hint;

        /**
         * Creates an autocomplete result.
         */
        public AutocompleteResult(List<Suggestion> suggestions, String hint) {
            this.suggestions = suggestions;
            this.hint = hint;
        }

        /**
         * Returns ranked suggestions for the current token context.
         */
        public List<Suggestion> getSuggestions() {
            return suggestions;
        }

        /**
         * Returns the field guidance text for the currently selected command scope.
         */
        public String getHint() {
            return hint;
        }
    }

    /**
     * A suggestion item shown in the command box dropdown.
     */
    public static class Suggestion {
        private final String value;
        private final SuggestionMode mode;

        /**
         * Creates a suggestion item.
         */
        public Suggestion(String value, SuggestionMode mode) {
            this.value = value;
            this.mode = mode;
        }

        /**
         * Returns the suggestion text displayed to the user.
         */
        public String getValue() {
            return value;
        }

        /**
         * Applies this suggestion to user input and returns the completed command text.
         */
        public String applyTo(String inputText) {
            String rawInput = inputText == null ? "" : inputText;
            TokenState tokenState = TokenState.from(rawInput);

            if (mode == SuggestionMode.COMMAND) {
                return value + " ";
            }

            if (tokenState.tokens.isEmpty()) {
                return rawInput;
            }

            String command = tokenState.tokens.get(0).toLowerCase(Locale.ROOT);
            return command + " " + value + " ";
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Suggestion)) {
                return false;
            }
            Suggestion suggestion = (Suggestion) other;
            return Objects.equals(value, suggestion.value) && mode == suggestion.mode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, mode);
        }
    }

    private enum SuggestionMode {
        COMMAND,
        SUBCOMMAND
    }

    private static class CommandSpec {
        private final FieldSpec fieldSpec;
        private final Map<String, FieldSpec> subcommands;

        private CommandSpec(FieldSpec fieldSpec, Map<String, FieldSpec> subcommands) {
            this.fieldSpec = fieldSpec;
            this.subcommands = subcommands;
        }

        private static CommandSpec of(FieldSpec fieldSpec) {
            return new CommandSpec(fieldSpec, Collections.emptyMap());
        }

        private static CommandSpec withSubcommands(Map<String, FieldSpec> subcommands) {
            return new CommandSpec(FieldSpec.of(listOf(), listOf()), subcommands);
        }
    }

    private static class FieldSpec {
        private final List<String> required;
        private final List<String> optional;

        private FieldSpec(List<String> required, List<String> optional) {
            this.required = required;
            this.optional = optional;
        }

        private static FieldSpec of(List<String> required, List<String> optional) {
            return new FieldSpec(required, optional);
        }
    }

    private static class SuggestionMatch {
        private final String value;
        private final int rank;

        private SuggestionMatch(String value, int rank) {
            this.value = value;
            this.rank = rank;
        }
    }

    private static class TokenState {
        private final List<String> tokens;
        private final boolean endsWithWhitespace;

        private TokenState(List<String> tokens, boolean endsWithWhitespace) {
            this.tokens = tokens;
            this.endsWithWhitespace = endsWithWhitespace;
        }

        private static TokenState from(String inputText) {
            String trimmedLeading = inputText.stripLeading();
            boolean endsWithWhitespace = !inputText.isEmpty()
                    && Character.isWhitespace(inputText.charAt(inputText.length() - 1));

            if (trimmedLeading.isEmpty()) {
                return new TokenState(Collections.emptyList(), endsWithWhitespace);
            }

            List<String> tokens = Arrays.asList(trimmedLeading.split("\\s+"));
            return new TokenState(tokens, endsWithWhitespace);
        }
    }
}
