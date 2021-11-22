package app;

import java.util.*;
import java.util.stream.Collectors;
/**
* How to run:-
 *  Run this (ElevatorSystem) main method using run as java application, JDK 8+ Required
 *
 * Assumptions:-
*   - Only Steady {@link Direction} elevators are considered for calculateDistanceFromEachElevator in this version
 *  - Output is printed on console from method checkAvailableElevators, it can be adapted as per requirement
 *  - To save time unit test are not created
 *  - Testing scenarios where not clear so added 2 cases in main method, which prints output
 */
public class ElevatorSystem {

    private List<Elevator> elevators = new ArrayList<>();
    private List<Request> upRequests = new ArrayList<>();
    private List<Request> downRequests = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("-- Case.1) All are at ground floor: up Requests 0 to 6 && Down Requests are from 9,4,7");
        ElevatorSystem system = new ElevatorSystem();
        system.registerElevators("Elevator1", 0);
        system.registerElevators("Elevator2", 0);
        system.registerElevators("Elevator3", 0);

        system.addRequest(0, 6, Direction.UP);
        system.addRequest(9, 0, Direction.DOWN);
        system.addRequest(4, 0, Direction.DOWN);
        system.addRequest(7, 0, Direction.DOWN);

        system.checkAvailableElevators();

        System.out.println("\n -- Case.2) Elevators are at 2,5,8: up Requests from 0 to 6, 0 to 3  && Down Requests are from 6, 4 ,7");
        system = new ElevatorSystem();
        system.registerElevators("Elevator1", 2);
        system.registerElevators("Elevator2", 5);
        system.registerElevators("Elevator3", 8);

        system.addRequest(0, 6, Direction.UP);
        system.addRequest(0, 3, Direction.UP);
        system.addRequest(6, 0, Direction.DOWN);
        system.addRequest(4, 0, Direction.DOWN);
        system.addRequest(7, 0, Direction.DOWN);

        system.checkAvailableElevators();
    }

    public void registerElevators(String id, int position) {
        elevators.add(new Elevator(id, position,Direction.STEADY));
    }

    public void addRequest(int from, int to, Direction direction) {
        Request request = new Request(from, to, direction);
        if (Direction.DOWN.equals(direction))
            downRequests.add(request);
        else
            upRequests.add(request);
    }

    public void checkAvailableElevators() {

        upRequests.sort(Comparator.comparing(Request::getTo));
        downRequests.sort(Comparator.comparing(Request::getFrom).reversed());

        //find distance of all elevators from the lowest floor in up direction request, and distance of all elevators from the highest floor in down direction request
        Map<Elevator, Integer> elevatorDistanceUp = calculateDistanceFromEachElevator(upRequests);
        Map<Elevator, Integer> elevatorDistanceDown = calculateDistanceFromEachElevator(downRequests);

        Map.Entry<Elevator, Integer> upClosestElevator = null;
        Map.Entry<Elevator, Integer> downClosestElevator = null;

        //find closest elevator for up direction
        if (upRequests.size() > 0) {
            upClosestElevator = elevatorDistanceUp.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).orElse(null);
        }

        //find closest elevator for down direction
        if (downRequests.size() > 0) {
            downClosestElevator = elevatorDistanceDown.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).orElse(null);
        }

        //find new elevator if same elevator is closest for up and down direction
        if (upRequests.size() > 0 && downRequests.size() > 0 && upClosestElevator != null && downClosestElevator != null && upClosestElevator.getKey().equals(downClosestElevator.getKey())) {

            if (upClosestElevator.getValue() < downClosestElevator.getValue()) {
                elevatorDistanceDown.remove(upClosestElevator.getKey());
                downClosestElevator = elevatorDistanceDown.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).orElse(null);
            } else {
                elevatorDistanceUp.remove(downClosestElevator.getKey());
                upClosestElevator = elevatorDistanceUp.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).orElse(null);
            }
        }

        Map<String, List> response = new HashMap<>();
        //set up stops for up direction lift
        if (upClosestElevator != null) {
            List<Integer> upStops = addIntermediateStopsForUpwardDirection(upClosestElevator);
            response.put(Direction.UP + " " + upClosestElevator.getKey().getId(), upStops);
        }

        //set up stops for down direction lift
        if (downClosestElevator != null) {
            List<Integer> downStops = addIntermediateStopsForDownwardDirection(downClosestElevator);
            response.put(Direction.DOWN + " " + downClosestElevator.getKey().getId(), downStops);
        }

        System.out.println("==OUTPUT==");
        System.out.println(response);
    }

    /*
     * Set stops in the directions
     * ClosestElevator_position > all downward stops pickup > last stop
     */
    private List<Integer> addIntermediateStopsForDownwardDirection(Map.Entry<Elevator, Integer> downClosestElevator) {
        List<Integer> downStops = downRequests.stream().map(Request::getFrom).collect(Collectors.toList());
        downStops.add(0, downClosestElevator.getKey().getPosition());
        downStops.add(downRequests.get(downRequests.size() - 1).getTo());
        return downStops;
    }

    /*
     * Set stops in the directions
     * ClosestElevator_position > all upward drop points
     */
    private List<Integer> addIntermediateStopsForUpwardDirection(Map.Entry<Elevator, Integer> upClosestElevator) {
        List<Integer> upStops = upRequests.stream().map(Request::getTo).collect(Collectors.toList());

        upStops.add(0, upClosestElevator.getKey().getPosition());
        upStops.add(1, upRequests.get(0).getFrom());
        return upStops;
    }

    /*
     * Currently only steady elevators are considered.
     */
    private Map<Elevator, Integer> calculateDistanceFromEachElevator(List<Request> requests) {
        Map<Elevator, Integer> elevatorDistance = new HashMap<>();
        if (requests.size() > 0) {
            int startFrom = requests.get(0).getFrom();
            elevatorDistance = elevators.stream().filter(e->Direction.STEADY.equals(e.getDirection())).collect(Collectors.toMap(e -> e, el -> Math.abs(el.getPosition() - startFrom)));
        }
        return elevatorDistance;
    }
}
