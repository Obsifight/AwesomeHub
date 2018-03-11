package me.awesomehub.hub.handlers;

import java.io.File;
import java.io.IOException;

/**
 * Handler abstrait permettant la création d'handlers supportant
 * les sauvegardes et les chargements.
 * 
 * @author Gwennael
 *
 */
public abstract class SavableHandler {

    /**
     * Le fichier à utiliser pour les sauvegardes.
     * 
     * @var {@link File}
     */
    protected File saveFile;

    /**
     * Construit un SavableHandler.
     * 
     * @param saveFile Le fichier à utiliser.
     */
    public SavableHandler(File saveFile) {
        this(saveFile, false);
    }

    /**
     * Construit un SavableHandler.
     * 
     * @param saveFile Le fichier à utiliser.
     * @param isDirectory Si le fichier est un dossier.
     */
    public SavableHandler(File saveFile, boolean isDirectory) {
        this.saveFile = saveFile;

        if (!saveFile.exists()) {
            if (isDirectory) {
                saveFile.mkdirs();
            } else {
                try {
                    saveFile.getParentFile().mkdirs();
                    saveFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.loadAll();
    }

    /**
     * Charge les données.
     */
    public abstract void loadAll();

    /**
     * Sauvegarde les données.
     */
    public abstract void saveAll();
}
