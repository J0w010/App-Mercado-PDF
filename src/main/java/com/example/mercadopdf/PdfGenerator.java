package com.example.mercadopdf;

import java.util.ArrayList;
import java.util.List;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.io.font.constants.StandardFonts;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfGenerator {

    public static List<String> gerar(List<Produto> produtos, List<String> tamanhos) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
        String pasta = "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\";
        new File(pasta).mkdirs();

        List<String> caminhos = new ArrayList<>();

        for (String t : tamanhos) {
            String caminho = pasta + "etiquetas_" + t + "_" + timestamp + ".pdf";

            PageSize pageSize = switch (t) {
                case "A5" -> PageSize.A5;
                case "A6" -> PageSize.A6.rotate();
                default   -> PageSize.A4;
            };

            PdfWriter writer = new PdfWriter(caminho);
            PdfDocument pdf = new PdfDocument(writer);

            for (Produto p : produtos) {
                String[] partes = p.preco.replace(",", ".").split("\\.");
                String reais = partes[0];
                String centavos = partes.length > 1 ? partes[1] : "00";
                if (centavos.length() == 1) centavos += "0";

                pdf.addNewPage(pageSize);
                gerarEtiqueta(pdf, p.nome, p.unidade, reais, centavos, pageSize);
            }

            pdf.close();
            caminhos.add(caminho);
        }

        return caminhos;
    }

    private static void gerarEtiqueta(PdfDocument pdf, String nome, String unidade,
                                      String reais, String centavos, PageSize tamanho) throws Exception {
        PdfCanvas canvas = new PdfCanvas(pdf.getLastPage());
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        float W = tamanho.getWidth();
        float H = tamanho.getHeight();
        float margem = W * 0.07f;
        float areaUtil = W - 2 * margem;

        // --- 1) NOME centralizado ---
        float tamanhoNome = 48f * (W / PageSize.A4.getWidth());
        float nomeWidth = bold.getWidth(nome.toUpperCase(), tamanhoNome);
        float nomeX = (W - nomeWidth) / 2f;
        float nomeY = H - margem - tamanhoNome;

        canvas.beginText()
                .setFontAndSize(bold, tamanhoNome)
                .moveText(nomeX, nomeY)
                .showText(nome.toUpperCase())
                .endText();

        // --- 2) R$ ---
        float tamanhoRS = 18f * (W / PageSize.A4.getWidth());
        float rsY = nomeY - tamanhoRS * 2.5f;

        canvas.beginText()
                .setFontAndSize(bold, tamanhoRS)
                .moveText(margem, rsY)
                .showText("R$")
                .endText();

        // --- 3) Reais — tamanho calculado para preencher largura ---
        float refSize = 100f;
        float refWidth = bold.getWidth(reais + ",", refSize);
        float tamanhoReais = areaUtil * refSize / refWidth * 0.8f;
        float tamanhoCentavos = tamanhoReais * 0.30f;

        // Limita pela altura disponível para não vazar
        float alturaDisponivel = H * 0.45f;
        if (tamanhoReais > alturaDisponivel) {
            tamanhoReais = alturaDisponivel;
        }

        // Calcula offsetX ANTES de usar
        float reaisWidth = bold.getWidth(reais + ",", tamanhoReais);
        float centavosWidth = bold.getWidth(centavos, tamanhoCentavos);
        float totalPrecoWidth = reaisWidth + centavosWidth;
        float offsetX = (W - totalPrecoWidth) / 2f;
        float centavosX = offsetX + reaisWidth;

        float reaisY = rsY - tamanhoReais * 1.05f;

        canvas.beginText()
                .setFontAndSize(bold, tamanhoReais)
                .moveText(offsetX, reaisY)
                .showText(reais + ",")
                .endText();

        // --- 4) Centavos — sobrescritos no topo direito dos reais ---
        float centavosY = reaisY + tamanhoReais * 0.45f;

        canvas.beginText()
                .setFontAndSize(bold, tamanhoCentavos * 0.7f)
                .moveText(centavosX, centavosY)
                .showText(centavos)
                .endText();

        // --- 5) Linha horizontal sob os centavos ---
        float linhaY = centavosY - tamanhoCentavos * 0.2f;
        float linhaFim = centavosX + bold.getWidth(centavos, tamanhoCentavos);

        canvas.setLineWidth(1.5f * (W / PageSize.A4.getWidth()))
                .moveTo(centavosX, linhaY)
                .lineTo(linhaFim, linhaY)
                .stroke();

        // --- 6) Unidade (KG, UN...) sob a linha ---
        float unidadeY = linhaY - tamanhoCentavos * 0.55f;

        canvas.beginText()
                .setFontAndSize(bold, tamanhoCentavos * 0.4f)
                .moveText(centavosX, unidadeY)
                .showText(unidade.toUpperCase())
                .endText();

        canvas.release();
    }
}