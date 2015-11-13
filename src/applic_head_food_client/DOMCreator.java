/**
 * DOMCreator
 *
 * Copyright (C) 2012 Sh1fT
 *
 * This file is part of Applic_Head_Food_Client.
 *
 * Applic_Head_Food_Client is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * Applic_Head_Food_Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Applic_Head_Food_Client; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package applic_head_food_client;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Manage a {@link DOMCreator}
 * @author Sh1fT
 */
public class DOMCreator {
    private String filename;
    private Document document;

    /**
     * Create a new {@link DOMCreator} instance
     * @param filename
     * @param parent 
     */
    public DOMCreator(String filename, Applic_Head_Food_Client parent) {
        this.setFilename(filename);
        this.setDocument(null);
        this.buildDOM(parent);
        this.saveDOM();
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Document getDocument() {
        return this.document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Build a DOM document
     * @param ahfc 
     */
    public void buildDOM(Applic_Head_Food_Client ahfc) {
        try {
            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
            fabrique.setValidating(true);
            DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            this.setDocument(constructeur.newDocument());
            // Propriétés du DOM
            this.getDocument().setXmlVersion("1.0");
//            this.getDocument().setXmlStandalone(true);
            this.getDocument().setDocumentURI(ahfc.getInputDTDFilename());
            // Création de l'arborescence du DOM
            Element racine = this.getDocument().createElement("Menu");
            racine.setAttribute("name", ahfc.getNomMenu());
            racine.setAttribute("vedetteDuJour", ahfc.getVedetteDuJour());
            Element entree = this.getDocument().createElement("Entree");
            entree.setAttribute("name", ahfc.getNomEntree());
            entree.setAttribute("type", ahfc.getTypeEntree());
            for (Integer i = 0; i < ahfc.getIngredientsEntreeListModel().getSize(); i++) {
                Element ingredient = this.getDocument().createElement("Ingredient");
                ingredient.setAttribute("value",
                        ahfc.getIngredientsEntreeListModel().get(i).toString());
                entree.appendChild(ingredient);
            }
            racine.appendChild(entree);
            Element plat = this.getDocument().createElement("Plat");
            plat.setAttribute("name", ahfc.getNomPlat());
            plat.setAttribute("type", ahfc.getTypePlat());
            for (Integer i = 0; i < ahfc.getIngredientsPlatListModel().getSize(); i++) {
                Element ingredient = this.getDocument().createElement("Ingredient");
                ingredient.setAttribute("value",
                        ahfc.getIngredientsPlatListModel().get(i).toString());
                plat.appendChild(ingredient);
            }
            racine.appendChild(plat);
            Element dessert = this.getDocument().createElement("Dessert");
            dessert.setAttribute("name", ahfc.getNomDessert());
            dessert.setAttribute("type", ahfc.getTypeDessert());
            for (Integer i = 0; i < ahfc.getIngredientsDessertListModel().getSize(); i++) {
                Element ingredient = this.getDocument().createElement("Ingredient");
                ingredient.setAttribute("value",
                        ahfc.getIngredientsDessertListModel().get(i).toString());
                dessert.appendChild(ingredient);
            }
            racine.appendChild(dessert);
            this.getDocument().appendChild(racine);
        } catch (ParserConfigurationException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     * Save a DOM document into an XML file
     */
    public void saveDOM() {
        try {
            // Création de la source DOM
            Source source = new DOMSource(this.getDocument());
            // Création du fichier de sortie
            Result resultat = new StreamResult(this.getFilename());
            // Configuration du transformer
            TransformerFactory fabrique = TransformerFactory.newInstance();
            Transformer transformer = fabrique.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            // Transformation
            transformer.transform(source, resultat);
        } catch(TransformerFactoryConfigurationError | IllegalArgumentException |
                TransformerException ex){
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}