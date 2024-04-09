import java.util.HashMap;
import java.util.Map;

enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST
}

class Maze {
    private Map<Integer, Room> rooms = new HashMap<>();

    public void addRoom(Room r) {
        rooms.put(r.getRoomNo(), r);
    }

    public Room roomNo(int r) {
        return rooms.get(r);
    }

    public void printMaze() {
        for (Room room : rooms.values()) {
            System.out.println("Room: " + room.getRoomNo());
            for (Direction direction : Direction.values()) {
                Wall wall = room.getSide(direction);
                System.out.println(direction + ": " + (wall instanceof DoorWall ? "Door" : "Wall"));
            }
            System.out.println();
        }
    }
}


class Room {
    private Map<Direction, Wall> sides = new HashMap<>();
    private int roomNo;

    public Room(int roomNo) {
        this.roomNo = roomNo;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public Wall getSide(Direction direction) {
        return sides.get(direction);
    }

    public void setSide(Direction direction, Wall wall) {
        sides.put(direction, wall);
    }
}

class Wall {
}

class DoorWall extends Wall {
    private Room r1;
    private Room r2;
    private boolean isOpen;

    public DoorWall(Room r1, Room r2) {
        this.r1 = r1;
        this.r2 = r2;
        this.isOpen = false;
    }
}

interface MazeBuilder {
    void buildRoom(int roomNo);

    void buildWall(Direction direction);

    void buildDoorWall(int room1No, int room2No);

    Maze getMaze();
}

class StandardMazeBuilder implements MazeBuilder {
    private Maze maze;

    StandardMazeBuilder() {
        maze = new Maze();
    }

    @Override
    public void buildRoom(int roomNo) {
        Room room = new Room(roomNo);
        maze.addRoom(room);
        room.setSide(Direction.NORTH, new Wall());
        room.setSide(Direction.EAST, new Wall());
        room.setSide(Direction.SOUTH, new Wall());
        room.setSide(Direction.WEST, new Wall());
    }

    @Override
    public void buildWall(Direction direction) {
    }

    @Override
    public void buildDoorWall(int room1No, int room2No) {
        Room r1 = maze.roomNo(room1No);
        Room r2 = maze.roomNo(room2No);
        DoorWall doorWall = new DoorWall(r1, r2);
        r1.setSide(Direction.NORTH, doorWall);
        r2.setSide(Direction.SOUTH, doorWall);
    }

    @Override
    public Maze getMaze() {
        return maze;
    }
}


class NewMazeBuilder implements MazeBuilder {
    private Maze maze;

    NewMazeBuilder() {
        maze = new Maze();
    }

    @Override
    public void buildRoom(int roomNo) {
        Room room = new Room(roomNo);
        maze.addRoom(room);
    }

    @Override
    public void buildWall(Direction direction) {
        if (direction == Direction.NORTH) {
            maze.roomNo(1).setSide(Direction.NORTH, new BrickWall());
        } else if (direction == Direction.SOUTH) {
            maze.roomNo(1).setSide(Direction.SOUTH, new IronWall());
        } else {
            maze.roomNo(1).setSide(direction, new Wall());
        }
    }

    @Override
    public void buildDoorWall(int room1No, int room2No) {
        Room room1 = maze.roomNo(room1No);
        Room room2 = maze.roomNo(room2No);
        room1.setSide(Direction.EAST, new WoodenDoor(room1, room2));
        room2.setSide(Direction.WEST, new WoodenDoor(room2, room1));
    }

    @Override
    public Maze getMaze() {
        return maze;
    }
}
class BrickWall extends Wall {
}

class IronWall extends Wall {
}

class WoodenDoor extends DoorWall {
    public WoodenDoor(Room r1, Room r2) {
        super(r1, r2);
    }
}


class GameDirector {
    private MazeBuilder builder;

    GameDirector(MazeBuilder builder) {
        this.builder = builder;
    }

    Maze construct() {
        builder.buildRoom(1);
        builder.buildRoom(2);
        builder.buildDoorWall(1, 2);
        return builder.getMaze();
    }
}
class MazeGame {
    public static void main(String[] argv) {
        MazeBuilder standardBuilder = new StandardMazeBuilder();
        MazeBuilder newBuilder = new NewMazeBuilder();

        createMaze(standardBuilder);
        createMaze(newBuilder);
    }

    private static void createMaze(MazeBuilder builder) {
        GameDirector director = new GameDirector(builder);
        Maze maze = director.construct();
        maze.printMaze();
    }
}

