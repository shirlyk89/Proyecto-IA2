package com.proyectospring.gestionbodega.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import com.proyectospring.gestionbodega.entities.EstadoOrden;
import com.proyectospring.gestionbodega.entities.OrdenCompra;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generarPdf(OrdenCompra orden) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                cs.beginText();
                cs.setFont(fontBold, 16);
                cs.newLineAtOffset(margin, y);
                cs.showText("Orden de Compra N. " + orden.getId());
                cs.endText();
                y -= 35;

                cs.setFont(fontRegular, 11);
                String[] lineas = {
                        "Fecha de creacion: " + orden.getFechaCreacion(),
                        "Proveedor: " + orden.getProveedor().getNombre(),
                        "Producto: " + orden.getProducto().getNombre(),
                        "Cantidad: " + orden.getCantidad(),
                        "Precio unitario: " + orden.getPrecioUnitario(),
                        "Total: " + orden.getTotal(),
                        "Bodega destino: " + orden.getBodegaDestino().getNombre(),
                        "Estado: " + orden.getEstado()
                };

                for (String linea : lineas) {
                    cs.beginText();
                    cs.newLineAtOffset(margin, y);
                    cs.showText(linea);
                    cs.endText();
                    y -= 20;
                }

                if (orden.getEstado() == EstadoOrden.BORRADOR) {
                    dibujarMarcaDeAgua(cs, fontBold, page);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el PDF de la orden", e);
        }
    }

    private void dibujarMarcaDeAgua(PDPageContentStream cs, PDType1Font font, PDPage page) throws IOException {
        PDExtendedGraphicsState gsTransparente = new PDExtendedGraphicsState();
        gsTransparente.setNonStrokingAlphaConstant(0.25f);
        cs.setGraphicsStateParameters(gsTransparente);
        cs.setNonStrokingColor(200, 0, 0);

        float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();

        cs.beginText();
        cs.setFont(font, 72);
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(45), width / 2f - 220, height / 2f - 100);
        cs.setTextMatrix(matrix);
        cs.showText("BORRADOR");
        cs.endText();

        // Restaurar opacidad y color normales para el resto del documento
        PDExtendedGraphicsState gsNormal = new PDExtendedGraphicsState();
        gsNormal.setNonStrokingAlphaConstant(1.0f);
        cs.setGraphicsStateParameters(gsNormal);
        cs.setNonStrokingColor(0, 0, 0);
    }
}
