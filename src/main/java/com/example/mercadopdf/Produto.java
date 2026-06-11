package com.example.mercadopdf;

public class Produto {
    public String nome;
    public String unidade;
    public String preco;

    public Produto(String nome, String unidade, String preco) {
        this.nome = nome;
        this.unidade = unidade;
        this.preco = preco;
    }

    @Override
    public String toString() {
        return nome + " | " + unidade + " | R$ " + preco;
    }
}