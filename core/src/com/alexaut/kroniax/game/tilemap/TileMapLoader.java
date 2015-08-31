package com.alexaut.kroniax.game.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class TileMapLoader {

    public TileMap load(FileHandle file) {
        TileMap map = new TileMap();

        String[] fileContent = file.readString().split("\\r?\\n");

        parseProperties(map, fileContent);
        parseTilesets(map, fileContent);
        parseTileLayers(map, fileContent);

        return map;
    }

    private void parseProperties(TileMap map, String[] file) {
        for (int i = 0; i < file.length; i++) {
            if (file[i].equalsIgnoreCase("[width]"))
                map.getProperties().add("width", Integer.parseInt(file[++i]));
            else if (file[i].equalsIgnoreCase("[height]"))
                map.getProperties().add("height", Integer.parseInt(file[++i]));
            else if (file[i].equalsIgnoreCase("[tilewidth]"))
                map.getProperties().add("tilewidth", Integer.parseInt(file[++i]));
            else if (file[i].equalsIgnoreCase("[tileheight]"))
                map.getProperties().add("tileheight", Integer.parseInt(file[++i]));
            else if (file[i].equalsIgnoreCase("[mapproperties]")) {
                while (!file[++i].equalsIgnoreCase("[/mapproperties]")) {
                    String[] property = file[i].split(" ");
                    if (property.length == 1)
                        map.getProperties().add(property[0], 0);
                    else if (property.length > 1)
                        map.getProperties().add(property[0], Integer.parseInt(property[1]));
                }
            }
        }
    }

    private void parseTilesets(TileMap map, String[] file) {

        boolean foundStartTag = false;

        for (int i = 0; i < file.length; i++) {
            if (file[i].equalsIgnoreCase("[tilesets]"))
                foundStartTag = true;
            else if (file[i].equalsIgnoreCase("[/tilesets]"))
                break;
            else if (foundStartTag) {
                String[] tilesetInfo = file[i].split(" ");

                String filePath = "data/sprites/" + tilesetInfo[0];
                FileHandle fileHandle = Gdx.files.internal(filePath);
                if (!fileHandle.exists())
                    fileHandle = Gdx.files.external(filePath);

                int firstID = Integer.parseInt(tilesetInfo[1]);
                int margin = Integer.parseInt(tilesetInfo[2]);
                int spacing = Integer.parseInt(tilesetInfo[3]);

                map.getTilesets().add(
                        new Tileset(fileHandle, firstID, margin, spacing, map.getTileWidth(), map.getTileHeight()));
            }
        }
    }

    private void parseTileLayers(TileMap map, String[] file) {
        boolean foundStartTag = false;
        for (int i = 0; i < file.length; i++) {
            if (file[i].equalsIgnoreCase("[layer]")) {
                foundStartTag = true;
                map.getTileLayers().add(new TileLayer(map.getWidth()));
                // Get name
                map.getTileLayers().get(map.getTileLayers().size() - 1).setName(file[++i]);
                i++; // Tile dimensions not used here
            } else if (file[i].equalsIgnoreCase("[/layer]"))
                break;
            else if (foundStartTag) {
                if (file[i].equalsIgnoreCase("[data]"))
                    i = parseLayerData(++i, file, map);
                else if (file[i].equalsIgnoreCase("[properties]"))
                    i = parseProperties(++i, file, map);
            }
        }
    }

    private int parseLayerData(int i, String[] file, TileMap map) {
        for (; i < file.length; i++) {
            if (file[i].equalsIgnoreCase("[/data]"))
                break;
            String[] colValues = file[i].split(" ");
            if (colValues.length > 1) {
                TileLayer layer = map.getTileLayers().get(map.getTileLayers().size() - 1);
                int startValue = Integer.parseInt(colValues[0]);
                layer.addColumn(startValue);

                int size = Integer.parseInt(colValues[1]);
                for (int j = 0; j < size; j++) {
                    int id = Integer.parseInt(colValues[2 + j]);
                    layer.addTile(map.getTileRegion(id));
                }
            }
        }
        return i;
    }

    private int parseProperties(int i, String[] file, TileMap map) {
        TileLayer activeLayer = map.getTileLayers().get(map.getTileLayers().size() - 1);

        for (; i < file.length; i++) {
            if (file[i].equalsIgnoreCase("[/properties]"))
                break;
            String[] property = file[i].split(" ");
            if (property.length == 1)
                activeLayer.getProperties().add(property[0], 0);
            else if (property.length > 1)
                activeLayer.getProperties().add(property[0], Integer.parseInt(property[1]));
        }
        return i;
    }
}