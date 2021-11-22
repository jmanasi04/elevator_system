package app;

import java.util.Objects;

public class Elevator {
    private String id;
    private int position;
    private Direction direction = Direction.STEADY;

    public Elevator(String id, int position,Direction direction) {
        this.id = id;
        this.position = position;
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Elevator elevator = (Elevator) o;
        return id.equals(elevator.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
