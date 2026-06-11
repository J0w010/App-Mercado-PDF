package com.example.mercadopdf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML private TextField nomeProduto;
    @FXML private TextField quilometragem;
    @FXML private TextField preco;
    @FXML private CheckBox checkA4;
    @FXML private CheckBox checkA5;
    @FXML private CheckBox checkA6;
    @FXML private ListView<Produto> listaProdutos;

    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listaProdutos.setItems(produtos);
    }

    @FXML
    protected void onAdicionarProduto() {
        String nome    = nomeProduto.getText().trim();
        String unidade = quilometragem.getText().trim();
        String pr      = preco.getText().trim();

        if (nome.isEmpty() || unidade.isEmpty() || pr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Preencha todos os campos antes de adicionar!");
            return;
        }

        produtos.add(new Produto(nome, unidade, pr));
        nomeProduto.clear();
        quilometragem.clear();
        preco.clear();
        nomeProduto.requestFocus();
    }

    @FXML
    protected void onRemoverProduto() {
        Produto selecionado = listaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            produtos.remove(selecionado);
        }
    }

    @FXML
    protected void RemoverTudoProduto() {
        produtos.clear();
    }

    @FXML
    protected void onGerarPdfClick() {
        if (produtos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Adicione ao menos um produto!");
            return;
        }

        List<String> tamanhosSelecionados = new ArrayList<>();
        if (checkA4.isSelected()) tamanhosSelecionados.add("A4");
        if (checkA5.isSelected()) tamanhosSelecionados.add("A5");
        if (checkA6.isSelected()) tamanhosSelecionados.add("A6");

        if (tamanhosSelecionados.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione ao menos um tamanho de folha!");
            return;
        }

        try {
            List<String> caminhos = PdfGenerator.gerar(new ArrayList<>(produtos), tamanhosSelecionados);
            StringBuilder msg = new StringBuilder("PDFs gerados em Documentos:\n");
            for (String c : caminhos) msg.append("• ").append(c).append("\n");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso!", msg.toString());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao gerar PDF:\n" + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}