package no.erlyberly.bootlegterraria.storage.impl;

import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import static no.erlyberly.bootlegterraria.storage.impl.Container.ContainerBuilder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContainerTest {

    @Test
    void addSameTypeTooManyValidOnly() {
        Container container = new ContainerBuilder(1).setValidOnly(false).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        assertEquals(0, container.add(tt, max));
        assertEquals(0, container.add(tt, max));
    }

    @Test
    void addSameTypeTooManyNotValid() {
        Container container = new ContainerBuilder(1).setValidOnly(true).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        assertEquals(0, container.add(tt, max));
        assertEquals(max, container.add(tt, max));
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    void addEarlyReturnDifferentTypeValidOnly() {
        Container container = new ContainerBuilder(1).setValidOnly(true).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        assertEquals(0, container.add(TileType.DIRT, max));
        assertEquals(max, container.add(tt, max));
    }

    @Test
    void addEarlyReturnDifferentTypeNotValid() {
        Container container = new ContainerBuilder(1).setValidOnly(false).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        assertEquals(0, container.add(TileType.DIRT, max));
        assertEquals(max, container.add(tt, max));
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    void addSpillageValidOnly() {
        Container container = new ContainerBuilder(2).setValidOnly(true).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        assertEquals(0, container.add(tt, max-1));
        assertEquals(0, container.add(tt, 2));

        assertArrayEquals( container.getContent(), new TileStack[]{new TileStack(tt,max),new TileStack(tt,1)});
    }

    @Test
    void addSpillageNotValidOnly() {
        Container container = new ContainerBuilder(2).setValidOnly(false).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        container.add(tt, max-1);
        container.add(tt, 2);

        assertArrayEquals( container.getContent(), new TileStack[]{new TileStack(tt,max+1),null});
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    void addFillNonFull() {
        Container container = new ContainerBuilder(1).build();
        TileType tt = TileType.CLOUD;
        int max = tt.getMaxStackSize();

        assertEquals(0, container.add(tt, max-1));
        assertEquals(0, container.add(tt, 1));

        assertArrayEquals(new TileStack[]{new TileStack(tt,max)},container.getContent());
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    void first(){
        Container container = new ContainerBuilder(1).build();
        TileType tt = TileType.CLOUD;

        assertEquals(-1, container.first(tt));

        container.add(tt,1);
        assertEquals(0, container.first(tt));

    }
}