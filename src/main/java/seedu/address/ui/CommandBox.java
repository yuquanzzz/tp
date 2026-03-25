package seedu.address.ui;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int MAX_VISIBLE_SUGGESTIONS = 8;
    private static final int SUGGESTION_MENU_GAP_PX = 6;

    private final CommandExecutor commandExecutor;
    private final CommandAutocomplete commandAutocomplete;
    private final ContextMenu suggestionMenu;

    private List<CommandAutocomplete.Suggestion> currentSuggestions;
    private int selectedSuggestionIndex;
    private double suggestionMenuBottomEdgeY;

    @FXML
    private TextField commandTextField;

    @FXML
    private Label commandHintLabel;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.commandAutocomplete = new CommandAutocomplete();
        this.suggestionMenu = new ContextMenu();
        this.suggestionMenu.setAutoFix(false);
        this.suggestionMenu.heightProperty().addListener((obs, oldHeight, newHeight) ->
            positionSuggestionMenuAboveCommandBox());
        this.currentSuggestions = List.of();
        this.selectedSuggestionIndex = 0;
        this.suggestionMenuBottomEdgeY = -1;

        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        commandTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                hideSuggestions();
            }
        });

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> {
            setStyleToDefault();
            refreshAutocomplete();
        });

        refreshAutocomplete();
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            if (applySelectedSuggestion()) {
                event.consume();
            }
            return;
        }

        if (event.getCode() == KeyCode.DOWN) {
            if (suggestionMenu.isShowing()) {
                moveSelection(1);
                event.consume();
            }
            return;
        }

        if (event.getCode() == KeyCode.UP) {
            if (suggestionMenu.isShowing()) {
                moveSelection(-1);
                event.consume();
            }
            return;
        }

        if (event.getCode() == KeyCode.ESCAPE) {
            if (suggestionMenu.isShowing()) {
                hideSuggestions();
                event.consume();
            }
            return;
        }

        if (event.getCode() == KeyCode.ENTER && suggestionMenu.isShowing()) {
            if (applySelectedSuggestion()) {
                event.consume();
            }
        }
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        if (suggestionMenu.isShowing() && applySelectedSuggestion()) {
            return;
        }

        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
            hideSuggestions();
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    private void refreshAutocomplete() {
        CommandAutocomplete.AutocompleteResult result = commandAutocomplete.evaluate(commandTextField.getText());
        commandHintLabel.setText(result.getHint());
        updateSuggestions(result.getSuggestions());
    }

    private void updateSuggestions(List<CommandAutocomplete.Suggestion> suggestions) {
        currentSuggestions = suggestions.stream()
            .limit(MAX_VISIBLE_SUGGESTIONS)
            .toList();
        selectedSuggestionIndex = currentSuggestions.isEmpty() ? -1 : 0;

        suggestionMenu.getItems().clear();
        if (currentSuggestions.isEmpty()) {
            hideSuggestions();
            return;
        }

        for (int i = 0; i < currentSuggestions.size(); i++) {
            CommandAutocomplete.Suggestion suggestion = currentSuggestions.get(i);
            Label label = new Label(suggestion.getValue());
            label.getStyleClass().add("command-suggestion-item");
            CustomMenuItem menuItem = new CustomMenuItem(label, true);
            final int suggestionIndex = i;
            menuItem.setOnAction(event -> applySuggestion(suggestionIndex));
            suggestionMenu.getItems().add(menuItem);
        }

        updateSelectionStyles();
        captureSuggestionMenuBottomEdge();
        if (!suggestionMenu.isShowing()) {
            suggestionMenu.show(commandTextField, Side.TOP, 0, 0);
        }
        positionSuggestionMenuAboveCommandBox();
    }

    private void captureSuggestionMenuBottomEdge() {
        Bounds textFieldBounds = commandTextField.localToScreen(commandTextField.getBoundsInLocal());
        if (textFieldBounds != null) {
            suggestionMenuBottomEdgeY = textFieldBounds.getMinY() - SUGGESTION_MENU_GAP_PX;
        }
    }

    private void positionSuggestionMenuAboveCommandBox() {
        Bounds textFieldBounds = commandTextField.localToScreen(commandTextField.getBoundsInLocal());
        if (textFieldBounds == null) {
            return;
        }

        if (suggestionMenuBottomEdgeY < 0) {
            suggestionMenuBottomEdgeY = textFieldBounds.getMinY() - SUGGESTION_MENU_GAP_PX;
        }

        double menuHeight = suggestionMenu.getHeight();
        if (menuHeight <= 0) {
            return;
        }

        suggestionMenu.setAnchorX(textFieldBounds.getMinX());
        suggestionMenu.setAnchorY(suggestionMenuBottomEdgeY - menuHeight);
    }

    private void hideSuggestions() {
        suggestionMenu.hide();
        suggestionMenuBottomEdgeY = -1;
    }

    private void moveSelection(int delta) {
        if (currentSuggestions.isEmpty()) {
            return;
        }

        int size = currentSuggestions.size();
        selectedSuggestionIndex = (selectedSuggestionIndex + delta + size) % size;
        updateSelectionStyles();
    }

    private void updateSelectionStyles() {
        for (int i = 0; i < suggestionMenu.getItems().size(); i++) {
            CustomMenuItem item = (CustomMenuItem) suggestionMenu.getItems().get(i);
            Label label = (Label) item.getContent();
            if (i == selectedSuggestionIndex) {
                if (!label.getStyleClass().contains("command-suggestion-item-selected")) {
                    label.getStyleClass().add("command-suggestion-item-selected");
                }
            } else {
                label.getStyleClass().remove("command-suggestion-item-selected");
            }
        }
    }

    private boolean applySelectedSuggestion() {
        if (currentSuggestions.isEmpty()) {
            return false;
        }

        int index = selectedSuggestionIndex < 0 ? 0 : selectedSuggestionIndex;
        applySuggestion(index);
        return true;
    }

    private void applySuggestion(int index) {
        if (index < 0 || index >= currentSuggestions.size()) {
            return;
        }

        CommandAutocomplete.Suggestion selected = currentSuggestions.get(index);
        commandTextField.setText(selected.applyTo(commandTextField.getText()));
        commandTextField.positionCaret(commandTextField.getText().length());
        refreshAutocomplete();
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
