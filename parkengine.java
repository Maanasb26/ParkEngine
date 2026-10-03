import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Marvellous Park Engine
 * Single-file Java Swing GUI version
 *
 * Compile:
 *     java parkengine.java
 *
 * Run:
 *     java parkengine.java
 *
 * Architecture retained:
 * Vehicle Factory
 * Parking Spot hierarchy
 * Observer Pattern
 * Strategy Pattern
 * Singleton Parking Lot
 * Parking Ticket
 * Entry Gate / Exit Gate
 */

enum VehicleType {
    BIKE, CAR, TRUCK
}

enum SpotType {
    BIKE, CAR, TRUCK
}

enum TicketStatus {
    ACTIVE, CLOSED
}

/* =========================
   VEHICLE HIERARCHY
   ========================= */

abstract class Vehicle {
    private String vehicleNumber;
    private VehicleType vehicleType;

    public Vehicle(String vehicleNumber, VehicleType vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    public VehicleType getvehicleType() {
        return this.vehicleType;
    }

    public String getvehicleNumber() {
        return this.vehicleNumber;
    }

    public abstract void display();
}

class Bike extends Vehicle {
    public Bike(String vehicleNumber) {
        super(vehicleNumber, VehicleType.BIKE);
    }

    @Override
    public void display() {
        System.out.println("Bike : " + getvehicleNumber());
    }
}

class Car extends Vehicle {
    public Car(String vehicleNumber) {
        super(vehicleNumber, VehicleType.CAR);
    }

    @Override
    public void display() {
        System.out.println("Car : " + getvehicleNumber());
    }
}

class Truck extends Vehicle {
    public Truck(String vehicleNumber) {
        super(vehicleNumber, VehicleType.TRUCK);
    }

    @Override
    public void display() {
        System.out.println("Truck : " + getvehicleNumber());
    }
}

/* =========================
   VEHICLE FACTORY
   ========================= */

class VehicleFactory {
    public static Vehicle creatVehicle(VehicleType type, String number) {
        switch (type) {
            case BIKE:
                return new Bike(number);
            case CAR:
                return new Car(number);
            case TRUCK:
                return new Truck(number);
            default:
                throw new IllegalArgumentException("Invalid Vehicle Type");
        }
    }
}

/* =========================
   PARKING SPOT HIERARCHY
   ========================= */

abstract class ParkingSpot {
    private int spotNumber;
    private SpotType spotType;
    private boolean occupied;
    private Vehicle vehicle;

    public ParkingSpot(int spotNumber, SpotType spotType) {
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.occupied = false;
        this.vehicle = null;
    }

    public int getSpotNumber() {
        return this.spotNumber;
    }

    public SpotType getsSpotType() {
        return this.spotType;
    }

    public boolean isOccupied() {
        return this.occupied;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public void parkVehicle(Vehicle vehicle) {
        if (this.occupied) {
            throw new RuntimeException("Parking Spot is already occupied");
        }

        this.vehicle = vehicle;
        this.occupied = true;
    }

    public Vehicle removeVehicle() {
        if (!this.occupied) {
            throw new RuntimeException("Parking Spot is already empty");
        }

        Vehicle temp = vehicle;
        this.vehicle = null;
        this.occupied = false;
        return temp;
    }

    public abstract boolean canFitVehicle(Vehicle vehicle);
}

class BikeSpot extends ParkingSpot {
    public BikeSpot(int spotNumber) {
        super(spotNumber, SpotType.BIKE);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle.getvehicleType() == VehicleType.BIKE;
    }
}

class CarSpot extends ParkingSpot {
    public CarSpot(int spotNumber) {
        super(spotNumber, SpotType.CAR);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle.getvehicleType() == VehicleType.CAR;
    }
}

class TruckSpot extends ParkingSpot {
    public TruckSpot(int spotNumber) {
        super(spotNumber, SpotType.TRUCK);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle.getvehicleType() == VehicleType.TRUCK;
    }
}

/* =========================
   OBSERVER
   ========================= */

interface ParkingObserver {
    void update();
}

/* =========================
   PARKING FLOOR
   ========================= */

class ParkingFloor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpots;
    private List<ParkingObserver> observers;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public int getfloorNumber() {
        return this.floorNumber;
    }

    public void addParkingSpot(ParkingSpot spot) {
        parkingSpots.add(spot);
    }

    public void addObserver(ParkingObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (ParkingObserver observer : observers) {
            observer.update();
        }
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : parkingSpots) {
            if (!spot.isOccupied() && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }

    public void occupySpot(ParkingSpot spot, Vehicle vehicle) {
        spot.parkVehicle(vehicle);
        notifyObservers();
    }

    public void releasespot(ParkingSpot spot) {
        spot.removeVehicle();
        notifyObservers();
    }

    public int getAvailableCount(SpotType type) {
        int count = 0;

        for (ParkingSpot spot : parkingSpots) {
            if (spot.getsSpotType() == type && !spot.isOccupied()) {
                count++;
            }
        }

        return count;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
}

/* =========================
   DISPLAY BOARD OBSERVER
   ========================= */

class Parkingdisplayboard implements ParkingObserver {
    private ParkingFloor floor;

    public Parkingdisplayboard(ParkingFloor floor) {
        this.floor = floor;
    }

    @Override
    public void update() {
        // GUI refresh is handled by the main GUI.
        // Observer is still retained as part of the architecture.
    }
}

/* =========================
   PARKING STRATEGY
   ========================= */

interface ParkingStrategy {
    ParkingSpot findspot(List<ParkingFloor> floors, Vehicle vehicle);
}

class FirstAvailableParkingStrategy implements ParkingStrategy {
    @Override
    public ParkingSpot findspot(List<ParkingFloor> floors, Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle);

            if (spot != null) {
                return spot;
            }
        }

        return null;
    }
}

/* =========================
   PRICING STRATEGY
   ========================= */

interface PricingStrategy {
    double CalculatePrice(Vehicle vehicle, long hours);
}

class NormalPricingStratey implements PricingStrategy {
    @Override
    public double CalculatePrice(Vehicle vehicle, long hours) {
        if (hours <= 0) {
            hours = 1;
        }

        switch (vehicle.getvehicleType()) {
            case BIKE:
                return hours * 20;
            case CAR:
                return hours * 50;
            case TRUCK:
                return hours * 100;
            default:
                return 0;
        }
    }
}

class WeekendPricingStratey implements PricingStrategy {
    @Override
    public double CalculatePrice(Vehicle vehicle, long hours) {
        if (hours <= 0) {
            hours = 1;
        }

        switch (vehicle.getvehicleType()) {
            case BIKE:
                return hours * 40;
            case CAR:
                return hours * 100;
            case TRUCK:
                return hours * 200;
            default:
                return 0;
        }
    }
}

/* =========================
   PAYMENT STRATEGY
   ========================= */

interface PayementStrategy {
    void pay(double amount);
}

class UPIPayement implements PayementStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("UPI Payment successful : Rs. " + amount);
    }
}

class CardPayement implements PayementStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Card Payment successful : Rs. " + amount);
    }
}

class CashPayement implements PayementStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Cash Payment successful : Rs. " + amount);
    }
}

/* =========================
   PARKING TICKET
   ========================= */

class ParkingTicket {
    private static int counter = 1000;

    private int ticketNumber;
    private Vehicle vehicle;
    private ParkingFloor floor;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private TicketStatus status;

    public ParkingTicket(Vehicle vehicle,
                          ParkingFloor floor,
                          ParkingSpot spot) {

        this.ticketNumber = ++counter;

        // FIXED:
        // The constructor parameter is now correctly named vehicle.
        this.vehicle = vehicle;

        this.floor = floor;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.status = TicketStatus.ACTIVE;
    }

    public int getTicketNumber() {
        return this.ticketNumber;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public ParkingFloor getFloor() {
        return this.floor;
    }

    public ParkingSpot getSpot() {
        return this.spot;
    }

    public LocalDateTime getEntryTime() {
        return this.entryTime;
    }

    public LocalDateTime getExittTime() {
        return this.exitTime;
    }

    public TicketStatus gTicketStatus() {
        return this.status;
    }

    public void closeTicket() {
        this.exitTime = LocalDateTime.now();
        this.status = TicketStatus.CLOSED;
    }

    public long calculateHours() {
        LocalDateTime endtime;

        if (exitTime == null) {
            endtime = LocalDateTime.now();
        } else {
            endtime = exitTime;
        }

        long minutes = Duration.between(entryTime, endtime).toMinutes();
        long hours = minutes / 60;

        if (minutes % 60 != 0) {
            hours++;
        }

        if (hours == 0) {
            hours = 1;
        }

        return hours;
    }
}

/* =========================
   ENTRY GATE
   ========================= */

class Entrygate {
    private int gateNumber;

    public Entrygate(int gateNumber) {
        this.gateNumber = gateNumber;
    }

    public int getGateNumber() {
        return this.gateNumber;
    }

    public ParkingTicket generateTicket(Vehicle vehicle,
                                         ParkingFloor floor,
                                         ParkingSpot spot) {
        return new ParkingTicket(vehicle, floor, spot);
    }
}

/* =========================
   EXIT GATE
   ========================= */

class ExitGate {
    private int gateNumber;

    public ExitGate(int gateNumber) {
        this.gateNumber = gateNumber;
    }

    public int getGateNumber() {
        return this.gateNumber;
    }

    public double processExit(ParkingTicket ticket,
                              PricingStrategy pricingStrategy,
                              PayementStrategy payementStrategy) {

        ticket.closeTicket();

        long hours = ticket.calculateHours();

        double amount =
                pricingStrategy.CalculatePrice(ticket.getVehicle(), hours);

        payementStrategy.pay(amount);

        return amount;
    }
}

/* =========================
   PARKING LOT SINGLETON
   ========================= */

class Parkinglot {
    private static Parkinglot instance;

    private String parkingLotName;
    private List<ParkingFloor> floors;
    private Map<Integer, ParkingTicket> activetickets;
    private Map<String, ParkingTicket> vehicleticketMap;

    private ParkingStrategy parkingStrategy;
    private PricingStrategy pricingStrategy;

    private Parkinglot() {
        floors = new ArrayList<>();
        activetickets = new HashMap<>();
        vehicleticketMap = new HashMap<>();

        parkingStrategy = new FirstAvailableParkingStrategy();
        pricingStrategy = new NormalPricingStratey();
    }

    public static synchronized Parkinglot getInstace() {
        if (instance == null) {
            instance = new Parkinglot();
        }

        return instance;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public List<ParkingFloor> getFloor() {
        return floors;
    }

    public void setParkingStrategy(ParkingStrategy strategy) {
        this.parkingStrategy = strategy;
    }

    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }

    public ParkingTicket parkvehicle(Vehicle vehicle,
                                      Entrygate entrygate) {

        if (vehicleticketMap.containsKey(vehicle.getvehicleNumber())) {
            throw new RuntimeException(
                    "This Vehicle is already parked");
        }

        ParkingSpot spot =
                parkingStrategy.findspot(floors, vehicle);

        if (spot == null) {
            throw new RuntimeException(
                    "No parking spot available for this vehicle type");
        }

        ParkingFloor selectedFloor = null;

        for (ParkingFloor floor : floors) {
            ParkingSpot temp =
                    floor.findAvailableSpot(vehicle);

            if (temp == spot) {
                selectedFloor = floor;
                break;
            }
        }

        if (selectedFloor == null) {
            throw new RuntimeException(
                    "Unable to identify parking floor");
        }

        selectedFloor.occupySpot(spot, vehicle);

        ParkingTicket ticket =
                entrygate.generateTicket(
                        vehicle,
                        selectedFloor,
                        spot);

        activetickets.put(
                ticket.getTicketNumber(),
                ticket);

        vehicleticketMap.put(
                vehicle.getvehicleNumber(),
                ticket);

        return ticket;
    }

    public double removeVehicle(int ticketNumber,
                                ExitGate exitGate,
                                PayementStrategy payementStrategy) {

        ParkingTicket ticket =
                activetickets.get(ticketNumber);

        if (ticket == null) {
            throw new RuntimeException(
                    "There is no such ticket");
        }

        double amount =
                exitGate.processExit(
                        ticket,
                        pricingStrategy,
                        payementStrategy);

        ticket.getFloor().releasespot(ticket.getSpot());

        activetickets.remove(ticketNumber);

        // FIXED:
        // The map key is the vehicle NUMBER, not vehicle CLASS.
        vehicleticketMap.remove(
                ticket.getVehicle().getvehicleNumber());

        return amount;
    }

    public ParkingTicket searchVehicle(String vehicleNumber) {
        return vehicleticketMap.get(vehicleNumber);
    }

    public ParkingTicket getActiveTicket(int ticketNumber) {
        return activetickets.get(ticketNumber);
    }

    public Collection<ParkingTicket> getActiveTickets() {
        return activetickets.values();
    }
}

/* =========================================================
   GUI APPLICATION
   ========================================================= */

public class parkengine extends JFrame {

    private Parkinglot parkinglot;
    private Entrygate entrygate;
    private ExitGate exitGate;

    private JLabel bikeCountLabel;
    private JLabel carCountLabel;
    private JLabel truckCountLabel;

    private JPanel parkingGrid;
    private JTextArea activityArea;

    private final Font TITLE_FONT =
            new Font("SansSerif", Font.BOLD, 28);

    private final Font HEADER_FONT =
            new Font("SansSerif", Font.BOLD, 18);

    private final Font NORMAL_FONT =
            new Font("SansSerif", Font.PLAIN, 14);

    public parkengine() {
        initializeParkingLot();
        buildGUI();
        refreshGUI();
    }

    /* =========================
       PARKING LOT INITIALIZATION
       ========================= */

    private void initializeParkingLot() {

        parkinglot = Parkinglot.getInstace();

        // Prevent duplicate floors if the application is
        // accidentally initialized more than once.
        if (parkinglot.getFloor().isEmpty()) {

            parkinglot.setParkingLotName(
                    "Marvellous Park Engine");

            ParkingFloor floor1 =
                    new ParkingFloor(1);

            floor1.addParkingSpot(
                    new BikeSpot(101));
            floor1.addParkingSpot(
                    new BikeSpot(102));

            floor1.addParkingSpot(
                    new CarSpot(103));
            floor1.addParkingSpot(
                    new CarSpot(104));

            floor1.addParkingSpot(
                    new TruckSpot(105));
            floor1.addParkingSpot(
                    new TruckSpot(106));

            Parkingdisplayboard board1 =
                    new Parkingdisplayboard(floor1);

            floor1.addObserver(board1);

            ParkingFloor floor2 =
                    new ParkingFloor(2);

            floor2.addParkingSpot(
                    new BikeSpot(201));
            floor2.addParkingSpot(
                    new BikeSpot(202));

            floor2.addParkingSpot(
                    new CarSpot(203));
            floor2.addParkingSpot(
                    new CarSpot(204));

            floor2.addParkingSpot(
                    new TruckSpot(205));
            floor2.addParkingSpot(
                    new TruckSpot(206));

            Parkingdisplayboard board2 =
                    new Parkingdisplayboard(floor2);

            floor2.addObserver(board2);

            parkinglot.addFloor(floor1);
            parkinglot.addFloor(floor2);
        }

        entrygate = new Entrygate(1);
        exitGate = new ExitGate(1);
    }

    /* =========================
       GUI
       ========================= */

    private void buildGUI() {

        setTitle("Marvellous Park Engine");
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setSize(1200, 780);
        setLocationRelativeTo(null);

        JPanel main =
                new JPanel(new BorderLayout(15, 15));

        main.setBorder(
                new EmptyBorder(15, 15, 15, 15));

        main.setBackground(
                new Color(245, 247, 250));

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createCenterPanel(), BorderLayout.CENTER);
        main.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel createHeader() {

        JPanel header =
                new JPanel(new BorderLayout());

        header.setOpaque(false);

        JLabel title =
                new JLabel("MARVELLOUS PARK ENGINE");

        title.setFont(TITLE_FONT);

        JLabel subtitle =
                new JLabel(
                        "Smart Parking Management System");

        subtitle.setFont(NORMAL_FONT);

        JPanel text =
                new JPanel();

        text.setOpaque(false);
        text.setLayout(
                new BoxLayout(
                        text,
                        BoxLayout.Y_AXIS));

        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);

        header.add(text, BorderLayout.WEST);

        JPanel counts =
                new JPanel(
                        new GridLayout(1, 3, 10, 0));

        counts.setOpaque(false);

        bikeCountLabel =
                createCountCard(
                        "BIKES",
                        "0");

        carCountLabel =
                createCountCard(
                        "CARS",
                        "0");

        truckCountLabel =
                createCountCard(
                        "TRUCKS",
                        "0");

        counts.add(bikeCountLabel.getParent());
        counts.add(carCountLabel.getParent());
        counts.add(truckCountLabel.getParent());

        header.add(counts, BorderLayout.EAST);

        return header;
    }

    private JLabel createCountCard(
            String title,
            String value) {

        JPanel card =
                new JPanel(
                        new BorderLayout());

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 214, 220)),
                        new EmptyBorder(
                                8, 15, 8, 15)));

        card.setBackground(Color.WHITE);

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        11));

        JLabel valueLabel =
                new JLabel(value,
                        SwingConstants.CENTER);

        valueLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24));

        card.add(titleLabel,
                BorderLayout.NORTH);

        card.add(valueLabel,
                BorderLayout.CENTER);

        return valueLabel;
    }

    private JPanel createCenterPanel() {

        JPanel center =
                new JPanel(
                        new BorderLayout(15, 15));

        center.setOpaque(false);

        center.add(
                createParkingGridPanel(),
                BorderLayout.CENTER);

        center.add(
                createActionPanel(),
                BorderLayout.EAST);

        return center;
    }

    private JPanel createParkingGridPanel() {

        JPanel wrapper =
                new JPanel(
                        new BorderLayout());

        wrapper.setBackground(Color.WHITE);

        wrapper.setBorder(
                BorderFactory.createTitledBorder(
                        "Parking Lot"));

        parkingGrid =
                new JPanel(
                        new GridLayout(
                                2,
                                1,
                                10,
                                10));

        parkingGrid.setBorder(
                new EmptyBorder(10, 10, 10, 10));

        parkingGrid.setBackground(
                Color.WHITE);

        wrapper.add(
                parkingGrid,
                BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createActionPanel() {

        JPanel panel =
                new JPanel();

        panel.setPreferredSize(
                new Dimension(250, 0));

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Operations"));

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS));

        JButton parkButton =
                createButton(
                        "Park Vehicle");

        JButton exitButton =
                createButton(
                        "Exit Vehicle");

        JButton searchButton =
                createButton(
                        "Search Vehicle");

        JButton displayButton =
                createButton(
                        "Refresh Parking Lot");

        JButton ticketsButton =
                createButton(
                        "Active Tickets");

        JButton pricingButton =
                createButton(
                        "Pricing");

        JButton closeButton =
                createButton(
                        "Exit Application");

        parkButton.addActionListener(
                e -> parkVehicleDialog());

        exitButton.addActionListener(
                e -> exitVehicleDialog());

        searchButton.addActionListener(
                e -> searchVehicleDialog());

        displayButton.addActionListener(
                e -> refreshGUI());

        ticketsButton.addActionListener(
                e -> showActiveTickets());

        pricingButton.addActionListener(
                e -> showPricing());

        closeButton.addActionListener(
                e -> System.exit(0));

        panel.add(parkButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(exitButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(searchButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(displayButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(ticketsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(pricingButton);
        panel.add(Box.createVerticalGlue());
        panel.add(closeButton);

        return panel;
    }

    private JButton createButton(String text) {

        JButton button =
                new JButton(text);

        button.setFont(NORMAL_FONT);

        button.setFocusPainted(false);

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45));

        return button;
    }

    private JPanel createBottomPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout());

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "System Activity"));

        activityArea =
                new JTextArea(5, 30);

        activityArea.setEditable(false);

        activityArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        12));

        activityArea.setLineWrap(true);

        activityArea.setWrapStyleWord(true);

        JScrollPane scroll =
                new JScrollPane(
                        activityArea);

        panel.add(
                scroll,
                BorderLayout.CENTER);

        return panel;
    }

    /* =========================
       GUI REFRESH
       ========================= */

    private void refreshGUI() {

        int bikes = 0;
        int cars = 0;
        int trucks = 0;

        for (ParkingFloor floor :
                parkinglot.getFloor()) {

            bikes +=
                    floor.getAvailableCount(
                            SpotType.BIKE);

            cars +=
                    floor.getAvailableCount(
                            SpotType.CAR);

            trucks +=
                    floor.getAvailableCount(
                            SpotType.TRUCK);
        }

        bikeCountLabel.setText(
                String.valueOf(bikes));

        carCountLabel.setText(
                String.valueOf(cars));

        truckCountLabel.setText(
                String.valueOf(trucks));

        refreshParkingGrid();

        log(
                "Parking lot refreshed. "
                        + "Available: "
                        + bikes + " bikes, "
                        + cars + " cars, "
                        + trucks + " trucks.");
    }

    private void refreshParkingGrid() {

        parkingGrid.removeAll();

        for (ParkingFloor floor :
                parkinglot.getFloor()) {

            JPanel floorPanel =
                    new JPanel(
                            new BorderLayout());

            floorPanel.setBackground(
                    Color.WHITE);

            JLabel floorLabel =
                    new JLabel(
                            " FLOOR "
                                    + floor.getfloorNumber());

            floorLabel.setFont(
                    HEADER_FONT);

            floorPanel.add(
                    floorLabel,
                    BorderLayout.NORTH);

            JPanel spotsPanel =
                    new JPanel(
                            new GridLayout(
                                    1,
                                    floor.getParkingSpots()
                                            .size(),
                                    8,
                                    8));

            spotsPanel.setBackground(
                    Color.WHITE);

            for (ParkingSpot spot :
                    floor.getParkingSpots()) {

                spotsPanel.add(
                        createSpotCard(spot));
            }

            floorPanel.add(
                    spotsPanel,
                    BorderLayout.CENTER);

            parkingGrid.add(floorPanel);
        }

        parkingGrid.revalidate();
        parkingGrid.repaint();
    }

    private JPanel createSpotCard(
            ParkingSpot spot) {

        JPanel card =
                new JPanel(
                        new BorderLayout());

        card.setBorder(
                BorderFactory.createLineBorder(
                        spot.isOccupied()
                                ? new Color(210, 80, 80)
                                : new Color(80, 170, 100),
                        2));

        card.setBackground(
                spot.isOccupied()
                        ? new Color(255, 235, 235)
                        : new Color(235, 250, 238));

        JLabel number =
                new JLabel(
                        "Spot "
                                + spot.getSpotNumber(),
                        SwingConstants.CENTER);

        number.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        15));

        JLabel type =
                new JLabel(
                        spot.getsSpotType().toString(),
                        SwingConstants.CENTER);

        type.setFont(NORMAL_FONT);

        JLabel status =
                new JLabel(
                        spot.isOccupied()
                                ? "<html><center>OCCUPIED<br>"
                                + spot.getVehicle()
                                .getvehicleNumber()
                                + "</center></html>"
                                : "AVAILABLE",
                        SwingConstants.CENTER);

        status.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12));

        card.add(
                number,
                BorderLayout.NORTH);

        card.add(
                type,
                BorderLayout.CENTER);

        card.add(
                status,
                BorderLayout.SOUTH);

        return card;
    }

    /* =========================
       PARK VEHICLE
       ========================= */

    private void parkVehicleDialog() {

        JComboBox<String> typeBox =
                new JComboBox<>(
                        new String[]{
                                "BIKE",
                                "CAR",
                                "TRUCK"
                        });

        JTextField numberField =
                new JTextField();

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                10,
                                10));

        panel.add(
                new JLabel("Vehicle Type:"));

        panel.add(typeBox);

        panel.add(
                new JLabel("Vehicle Number:"));

        panel.add(numberField);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Park Vehicle",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result !=
                JOptionPane.OK_OPTION) {
            return;
        }

        String number =
                numberField.getText()
                        .trim()
                        .toUpperCase();

        if (number.isEmpty()) {

            showError(
                    "Vehicle number cannot be empty.");

            return;
        }

        VehicleType vehicleType =
                VehicleType.valueOf(
                        typeBox.getSelectedItem()
                                .toString());

        try {

            Vehicle vehicle =
                    VehicleFactory.creatVehicle(
                            vehicleType,
                            number);

            ParkingTicket ticket =
                    parkinglot.parkvehicle(
                            vehicle,
                            entrygate);

            refreshGUI();

            log(
                    "Vehicle parked: "
                            + number
                            + " | "
                            + vehicleType
                            + " | Ticket #"
                            + ticket.getTicketNumber());

            showTicketDialog(ticket);

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    /* =========================
       TICKET DISPLAY
       ========================= */

    private void showTicketDialog(
            ParkingTicket ticket) {

        String message =
                "PARKING TICKET\n\n"
                        + "Ticket Number : "
                        + ticket.getTicketNumber()
                        + "\n"
                        + "Vehicle Number: "
                        + ticket.getVehicle()
                        .getvehicleNumber()
                        + "\n"
                        + "Vehicle Type  : "
                        + ticket.getVehicle()
                        .getvehicleType()
                        + "\n"
                        + "Floor Number  : "
                        + ticket.getFloor()
                        .getfloorNumber()
                        + "\n"
                        + "Spot Number   : "
                        + ticket.getSpot()
                        .getSpotNumber()
                        + "\n"
                        + "Entry Time    : "
                        + ticket.getEntryTime()
                        + "\n"
                        + "Status        : "
                        + ticket.gTicketStatus();

        JTextArea area =
                new JTextArea(message);

        area.setEditable(false);

        area.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14));

        area.setBorder(
                new EmptyBorder(
                        15, 15, 15, 15));

        JOptionPane.showMessageDialog(
                this,
                area,
                "Parking Ticket",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /* =========================
       EXIT VEHICLE
       ========================= */

    private void exitVehicleDialog() {

        JTextField ticketField =
                new JTextField();

        JComboBox<String> paymentBox =
                new JComboBox<>(
                        new String[]{
                                "Cash",
                                "UPI",
                                "Card"
                        });

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                10,
                                10));

        panel.add(
                new JLabel("Ticket Number:"));

        panel.add(ticketField);

        panel.add(
                new JLabel("Payment Method:"));

        panel.add(paymentBox);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Exit Vehicle",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result !=
                JOptionPane.OK_OPTION) {
            return;
        }

        try {

            int ticketNumber =
                    Integer.parseInt(
                            ticketField
                                    .getText()
                                    .trim());

            ParkingTicket ticket =
                    parkinglot.getActiveTicket(
                            ticketNumber);

            if (ticket == null) {
                throw new RuntimeException(
                        "No active ticket found.");
            }

            PayementStrategy paymentStrategy;

            switch (
                    paymentBox
                            .getSelectedItem()
                            .toString()) {

                case "Cash":
                    paymentStrategy =
                            new CashPayement();
                    break;

                case "UPI":
                    paymentStrategy =
                            new UPIPayement();
                    break;

                case "Card":
                    paymentStrategy =
                            new CardPayement();
                    break;

                default:
                    throw new RuntimeException(
                            "Invalid payment method.");
            }

            String vehicleNumber =
                    ticket.getVehicle()
                            .getvehicleNumber();

            VehicleType type =
                    ticket.getVehicle()
                            .getvehicleType();

            long hours =
                    ticket.calculateHours();

            double amount =
                    new NormalPricingStratey()
                            .CalculatePrice(
                                    ticket.getVehicle(),
                                    hours);

            parkinglot.removeVehicle(
                    ticketNumber,
                    exitGate,
                    paymentStrategy);

            refreshGUI();

            log(
                    "Vehicle exited: "
                            + vehicleNumber
                            + " | "
                            + type
                            + " | "
                            + "Duration: "
                            + hours
                            + " hour(s)"
                            + " | Amount: Rs. "
                            + amount);

            JOptionPane.showMessageDialog(
                    this,
                    "Vehicle Exit Successful!\n\n"
                            + "Vehicle : "
                            + vehicleNumber
                            + "\n"
                            + "Ticket  : "
                            + ticketNumber
                            + "\n"
                            + "Duration: "
                            + hours
                            + " hour(s)\n"
                            + "Amount  : Rs. "
                            + amount,
                    "Exit Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    /* =========================
       SEARCH VEHICLE
       ========================= */

    private void searchVehicleDialog() {

        JTextField numberField =
                new JTextField();

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        numberField,
                        "Enter Vehicle Number",
                        JOptionPane.OK_CANCEL_OPTION);

        if (result !=
                JOptionPane.OK_OPTION) {
            return;
        }

        String number =
                numberField.getText()
                        .trim()
                        .toUpperCase();

        if (number.isEmpty()) {
            return;
        }

        ParkingTicket ticket =
                parkinglot.searchVehicle(number);

        if (ticket == null) {

            showError(
                    "Vehicle "
                            + number
                            + " is not currently parked.");

            return;
        }

        showTicketDialog(ticket);

        log(
                "Vehicle searched: "
                        + number
                        + " | Ticket #"
                        + ticket.getTicketNumber());
    }

    /* =========================
       ACTIVE TICKETS
       ========================= */

    private void showActiveTickets() {

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "ACTIVE PARKING TICKETS\n");

        builder.append(
                "====================================\n\n");

        Collection<ParkingTicket> tickets =
                parkinglot.getActiveTickets();

        if (tickets.isEmpty()) {

            builder.append(
                    "No vehicles are currently parked.");

        } else {

            for (ParkingTicket ticket :
                    tickets) {

                builder.append(
                        "Ticket #")
                        .append(
                                ticket.getTicketNumber())
                        .append(" | Vehicle: ")
                        .append(
                                ticket.getVehicle()
                                        .getvehicleNumber())
                        .append(" | Type: ")
                        .append(
                                ticket.getVehicle()
                                        .getvehicleType())
                        .append(" | Floor: ")
                        .append(
                                ticket.getFloor()
                                        .getfloorNumber())
                        .append(" | Spot: ")
                        .append(
                                ticket.getSpot()
                                        .getSpotNumber())
                        .append("\n");
            }
        }

        JTextArea area =
                new JTextArea(
                        builder.toString());

        area.setEditable(false);

        area.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        13));

        JScrollPane scroll =
                new JScrollPane(area);

        scroll.setPreferredSize(
                new Dimension(
                        700,
                        350));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Active Tickets",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /* =========================
       PRICING
       ========================= */

    private void showPricing() {

        String message =
                "NORMAL PRICING\n\n"
                        + "Bike  : Rs. 20 / hour\n"
                        + "Car   : Rs. 50 / hour\n"
                        + "Truck : Rs. 100 / hour\n\n"
                        + "WEEKEND PRICING\n\n"
                        + "Bike  : Rs. 40 / hour\n"
                        + "Car   : Rs. 100 / hour\n"
                        + "Truck : Rs. 200 / hour";

        JOptionPane.showMessageDialog(
                this,
                message,
                "Pricing Strategy",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /* =========================
       HELPERS
       ========================= */

    private void log(String message) {

        if (activityArea == null) {
            return;
        }

        activityArea.append(
                "["
                        + LocalDateTime.now()
                        + "] "
                        + message
                        + "\n");

        activityArea.setCaretPosition(
                activityArea.getDocument()
                        .getLength());
    }

    private void showError(String message) {

        if (message == null ||
                message.trim().isEmpty()) {

            message = "Unknown error.";
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "Parking System Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /* =========================
       MAIN
       ========================= */

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    try {

                        UIManager.setLookAndFeel(
                                UIManager
                                        .getSystemLookAndFeelClassName());

                    } catch (Exception ignored) {
                    }

                    parkengine app =
                            new parkengine();

                    app.setVisible(true);
                });
    }
}
