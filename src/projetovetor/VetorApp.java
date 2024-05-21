package projetovetor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

public class VetorApp extends Application {

    private List<Vetor> vetores = new ArrayList<>(); // Lista para armazenar os vetores
    private ListView<String> listView = new ListView<>(); // Componente para exibir os vetores

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fundamentos Matemáticos - SENAI FATESG");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        // Cria a barra de menus
        MenuBar menuBar = new MenuBar();
        Menu menuVetor = new Menu("Vetor");
        Menu menuCalcular = new Menu("Calcular");

        // Adiciona os itens de menu para adicionar e visualizar/remover vetores
        menuVetor.getItems().addAll(criarMenuItemAdicionar(), criarMenuItemVisualizarRemover());

        // Adiciona os menus de cálculos para vetores 2D e 3D
        menuCalcular.getItems().addAll(criarMenuCalculos("Calcular 2D", 2), criarMenuCalculos("Calcular 3D", 3));
        menuBar.getMenus().addAll(menuVetor, menuCalcular);
        root.setTop(menuBar);

        // Configura a visualização da lista de vetores
        listView.setEditable(false);
        root.setCenter(listView);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Cria o item de menu para adicionar vetores
    private MenuItem criarMenuItemAdicionar() {
        MenuItem addItem = new MenuItem("Adicionar Vetor");
        addItem.setOnAction(e -> adicionarVetor());
        return addItem;
    }

    // Cria o item de menu para visualizar e remover vetores
    private MenuItem criarMenuItemVisualizarRemover() {
        MenuItem viewRemoveItem = new MenuItem("Visualizar/Remover Vetores");
        viewRemoveItem.setOnAction(e -> visualizarRemoverVetores());
        return viewRemoveItem;
    }

    // Cria o menu de cálculos para vetores de 2D ou 3D
    private Menu criarMenuCalculos(String title, int dimension) {
        Menu menu = new Menu(title);
        menu.getItems().addAll(
                criarMenuItem("Magnitude", () -> calcularUnicoVetor(dimension, this::calcularMagnitude)),
                criarMenuItem("Produto Escalar", () -> calcularDoisVetores(dimension, this::calcularProdutoEscalar)),
                criarMenuItem("Ângulo", () -> calcularDoisVetores(dimension, this::calcularAngulo)),
                criarMenuItem("Produto Vetorial", () -> calcularDoisVetores(dimension, this::calcularProdutoVetorial))
        );
        return menu;
    }

    // Cria um item de menu com uma ação associada
    private MenuItem criarMenuItem(String text, Runnable action) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(e -> action.run());
        return menuItem;
    }

    // Adiciona um novo vetor
    private void adicionarVetor() {
        Dialog<Vetor> dialog = criarDialogoVetor();
        Optional<Vetor> result = dialog.showAndWait();
        result.ifPresent(vetor -> {
            vetores.add(vetor);
            updateListView();
        });
    }

    // Cria o diálogo para entrada de um novo vetor
    private Dialog<Vetor> criarDialogoVetor() {
        Dialog<Vetor> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Vetor");
        dialog.setHeaderText("Insira as coordenadas do vetor:");

        VBox vbox = new VBox();
        TextField xField = new TextField();
        xField.setPromptText("X");
        TextField yField = new TextField();
        yField.setPromptText("Y");
        TextField zField = new TextField();
        zField.setPromptText("Z (opcional)");

        vbox.getChildren().addAll(new Label("X:"), xField, new Label("Y:"), yField, new Label("Z:"), zField);
        dialog.getDialogPane().setContent(vbox);

        ButtonType buttonTypeOk = new ButtonType("Adicionar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);

        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                Double x = Double.parseDouble(xField.getText());
                Double y = Double.parseDouble(yField.getText());
                Double z = zField.getText().isEmpty() ? null : Double.parseDouble(zField.getText());
                return new Vetor(x, y, z);
            }
            return null;
        });
        return dialog;
    }

    // Visualiza e remove vetores da lista
    private void visualizarRemoverVetores() {
        ChoiceDialog<Vetor> dialog = new ChoiceDialog<>(null, vetores);
        dialog.setTitle("Visualizar/Remover Vetores");
        dialog.setHeaderText("Selecione um vetor para remover:");
        dialog.setContentText("Vetores:");
        Optional<Vetor> result = dialog.showAndWait();
        result.ifPresent(vetor -> {
            vetores.remove(vetor);
            updateListView();
        });
    }

    // Calcula operações que requerem um único vetor (para 2D ou 3D)
    private void calcularUnicoVetor(int dimension, Consumer<Vetor> operation) {
        ChoiceDialog<Vetor> dialog = new ChoiceDialog<>(null, getVetoresByDimension(dimension));
        dialog.setTitle("Seleção de Vetor");
        dialog.setHeaderText("Selecione um vetor para calcular:");
        dialog.setContentText("Vetores:");

        // Altera o texto do botão para "Calcular"
        ButtonType buttonTypeCalcular = new ButtonType("Calcular", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypeCalcular, ButtonType.CANCEL);

        Optional<Vetor> result = dialog.showAndWait();
        result.ifPresent(operation::accept);
    }

    // Calcula operações que requerem dois vetores (para 2D ou 3D)
    private void calcularDoisVetores(int dimension, BiConsumer<Vetor, Vetor> operation) {
        Dialog<Pair<Vetor, Vetor>> dialog = new Dialog<>();
        dialog.setTitle("Seleção de Vetores para Cálculos");

        ComboBox<Vetor> comboBoxVetores1 = new ComboBox<>(FXCollections.observableArrayList(getVetoresByDimension(dimension)));
        ComboBox<Vetor> comboBoxVetores2 = new ComboBox<>(FXCollections.observableArrayList(getVetoresByDimension(dimension)));
        comboBoxVetores1.setConverter(new StringConverter<Vetor>() {
            @Override
            public String toString(Vetor vetor) {
                return vetor == null ? "Nenhum" : vetor.toString();
            }

            @Override
            public Vetor fromString(String string) {
                return null; // Não necessário
            }
        });

        comboBoxVetores2.setConverter(new StringConverter<Vetor>() {
            @Override
            public String toString(Vetor vetor) {
                return vetor == null ? "Nenhum" : vetor.toString();
            }

            @Override
            public Vetor fromString(String string) {
                return null; // Não necessário
            }
        });

        VBox vBox = new VBox(10, new Label("Selecione o primeiro vetor:"), comboBoxVetores1, new Label("Selecione o segundo vetor:"), comboBoxVetores2);
        dialog.getDialogPane().setContent(vBox);

        // Altera o texto do botão para "Calcular"
        ButtonType buttonTypeCalcular = new ButtonType("Calcular", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCalcular, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeCalcular) {
                return new Pair<>(comboBoxVetores1.getValue(), comboBoxVetores2.getValue());
            }
            return null;
        });

        Optional<Pair<Vetor, Vetor>> result = dialog.showAndWait();
        result.ifPresent(pares -> {
            Vetor v1 = pares.getKey();
            Vetor v2 = pares.getValue();
            if (v1 != null && v2 != null) {
                operation.accept(v1, v2);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro de Seleção");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, selecione dois vetores válidos para o cálculo.");
                alert.showAndWait();
            }
        });
    }

    // Filtra a lista de vetores para retornar apenas os vetores da dimensão especificada (2D ou 3D)
    private List<Vetor> getVetoresByDimension(int dimension) {
        List<Vetor> filteredList = new ArrayList<>();
        for (Vetor vetor : vetores) {
            if ((dimension == 2 && vetor.getZ() == null) || (dimension == 3 && vetor.getZ() != null)) {
                filteredList.add(vetor);
            }
        }
        return filteredList;
    }

    // Calcula e exibe a magnitude de um vetor
    private void calcularMagnitude(Vetor v) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Magnitude do Vetor: %.2f", v.calcularMagnitude()));
        alert.showAndWait();
    }

    // Calcula e exibe o produto escalar de dois vetores
    private void calcularProdutoEscalar(Vetor v1, Vetor v2) {
        double resultado = Vetor.calcularProdutoEscalar(v1, v2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Produto Escalar: %.2f", resultado));
        alert.showAndWait();
    }

    // Calcula e exibe o ângulo entre dois vetores
    private void calcularAngulo(Vetor v1, Vetor v2) {
        double angulo = Vetor.calcularAngulo(v1, v2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Ângulo entre Vetores: %.2f", angulo));
        alert.showAndWait();
    }

    // Calcula e exibe o produto vetorial de dois vetores
    private void calcularProdutoVetorial(Vetor v1, Vetor v2) {
        Vetor resultado = Vetor.calcularProdutoVetorial(v1, v2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText("Produto Vetorial: " + resultado);
        alert.showAndWait();
    }

    // Atualiza a exibição da lista de vetores
    private void updateListView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        vetores.forEach(v -> items.add(v.toString()));
        listView.setItems(items);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
