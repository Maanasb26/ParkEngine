/*
    Parkinlot Automation Systems
    Step 1 - Create Required Enums 
    Step 2 - Vehicle Hierarchy creation 
    Step 3 - Vehicle Factory Creation (Factory Design Pattern)
    Step 4 - Parkingspot Hierarchy 
    Step 5 - Parking Observer class 
    Step 6 - ParkingFloor Class 
    Step 7 - Parking Display Board  (Observer Pattern)
    Step 8 - Parking Strategy Class (Strategy Pattern)
    Step 9 - Pricing Strategy Class (Strategy Pattern )
    Step 10 - Payement Strategy Class 
    Step 11 - Parking Ticket Class 
    Step 12 - Entrygate class 
    step 13 - ExitGate class 
    Step 14 - ParkingLot class ( Singleton Pattern)
    Step 15 -  Main Class Controller 
*/


import java.util.*;
import java.security.KeyStore.Entry;
import java.time.Duration; // Calculates Difference Between Two Diff Dates or Time 
import java.time.LocalDateTime; // Gives system time 


////////////////////////////////////////////
/// 
///  Step 1 : Create Enums 
/// Used To Create Fixed Constants Which are 
/// requried throughout the project  
/// 
/// ///////////////////////////////////////


// Represents the Different Types of Vehicles Supported BY the Project 
enum VehicleType 
{
    BIKE,
    CAR,
    TRUCK 
}
// Represents the Different Types of Parking Spots 
enum SpotType 
{
    BIKE,
    CAR,
    TRUCK 
}

// Represents Current State of Parking Ticket 
enum TicketStatus 
{
    ACTIVE,
    CLOSED
}


////////////////////////////////////////////
/// 
///  Step 2 : Create Vehicle Class Hierarchy  
///  Used To Create Multiple Types of Classes 
///  Which Represents the types of Vehicles 
/// 
/// Concepts : Abstraction, INgeritance, Polymorphism, Encapsulation
/// 
/// ///////////////////////////////////////


// Hiding Implementation Details from outside world is abstraction 

// Class Which Represents A generic Vehicle Type
abstract class Vehicle
{

    // Abstracted (Hidden) Characteristics of class 
    private String vehicleNumber;
    private VehicleType vehicleType;

    // Parameterised Constructor 

    public Vehicle(String vehicleNumber, VehicleType vehicleType)
    {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    // Concrete Getter Methods 

    public VehicleType getvehicleType()
    {
        return this.vehicleType;
    }

    public String getvehicleNumber()
    {
        return this.vehicleNumber;
    }

    // Every Concrete Class will provide its own defination
    public abstract void display();
}

// Class which represents the vehicle type as car 

class Bike extends Vehicle
{
    // Parameterised Contructor 
    public Bike (String vehicleNumber )
    {
        // Calls Vehicle class cosntructor 
        super(vehicleNumber,VehicleType.BIKE);
    }

    // Method Overriding 
    @Override 
    public void display()
    {
        System.out.println("Bike : "+ getvehicleNumber());
    }
}

// Class which represents the vehicle type as car 

class Car extends Vehicle
{
    // Parameterised Contructor 
    public Car (String vehicleNumber )
    {
        // Calls Vehicle class cosntructor 
        super(vehicleNumber,VehicleType.CAR);
    }

    // Method Overriding 
    @Override 
    public void display()
    {
        System.out.println("Car : "+ getvehicleNumber());
    }
}

// Class which represents the vehicle type as truck 
class Truck extends Vehicle
{
    // Parameterised Contructor 
    public Truck (String vehicleNumber )
    {
        // Calls Vehicle class cosntructor 
        super(vehicleNumber,VehicleType.TRUCK);
    }

    // Method Overriding 
    @Override 
    public void display()
    {
        System.out.println("Truck : "+ getvehicleNumber());
    }
}


////////////////////////////////////////////
/// 
///  Step 3 : Create Vehicle Factory Class 
///  Used to Centralise the Creation of Vehicle 
///  objects 
/// 
/// Concepts : Factory Design Pattern 
/// 
/// ///////////////////////////////////////



class VehicleFactory 
{
    // Created and Returns the Desired Class Object 
    public static Vehicle creatVehicle(VehicleType type, String number)
    {

        switch (type)
        {
            case BIKE:
                return new Bike(number);
            case CAR:
                return new Car(number);
            case TRUCK:
                return new Truck(number);
            default:
                throw new IllegalArgumentException("Invalid Vehicle Type "); // Do not return, Throw an Exception - because Return argument for the class is vehicle not an exception 
        }
    }
}


////////////////////////////////////////////
/// 
///  Step 4 : Create Parking Spot Hierarchy  
///  Used to create hierarchy of parking spots 
///   
/// 
/// Concepts : Encapsulation, Abstration, Polymorphism, INheritance  
/// 
/// ///////////////////////////////////////

abstract class ParkingSpot 
{
    // Unique Number for Parking Spot (Primary Key) 
    private int spotNumber;

    // Type of parking Spot 
    private SpotType spotType;

    // Indicated wether spot is currently occupied or not 
    private boolean occupied ;

    // Stores Information about the vehicle 

    private Vehicle vehicle ; // Composition : Creating Object of another class inside another class, no extend keyword still composition 


    // Parameterised Consturctor 
    public ParkingSpot(int spotNumber, SpotType spotType )
    {
        this.spotNumber = spotNumber;
        this.spotType = spotType;

        // Initialaised with Default Values 
        this.occupied = false; // false - shows that  spot is not occupied 
        this.vehicle = null; // Null because, we do not know what is the vehicle type 
    }

    public int getSpotNumber ()
    {
        return this.spotNumber;
    }

    public SpotType getsSpotType ()
    {
        return this.spotType;
    }
    public boolean isOccupied()
    {
        return this.occupied;
    }
    public Vehicle getVehicle()
    {
        return this.vehicle;
    }

    // It is used to park the vehicle 
    public void parkVehicle(Vehicle vehicle)
    {
        if(this.occupied== true) // Spot is occumpied 
        {
            throw new RuntimeException("Parking Spot is already Occupied ");
        }
        else // Spot is empty 
        {
            this.vehicle = vehicle;
            this.occupied = true;
        }
    }

    public  Vehicle removeVehicle () // program should specify that Car or bike ( vehicle type ) exited 
    {
        if(this.occupied== true)
        {
            Vehicle  temp = vehicle;
            this.vehicle = null;
            this.occupied = false;

            return temp;

        }
        else 
        {
            throw new RuntimeException("Parking Spot is already empty ");
        }


    }

    // This Method Decides Whether we can park the vehicle in spot or not 
    public abstract boolean canFitVehicle( Vehicle vehicle);

    public void display ()
    {
        System.out.println("Spot : "+ spotNumber + " [ "+ spotType + " ] ");

        if(this.occupied==true)
        {
            System.out.println("Occupied By : "+ vehicle.getvehicleNumber());
        }
        else 
        {
            System.out.println("Spot is Available ");
        }
    }

} // End of ParkingSpot class 

class BikeSpot extends ParkingSpot
{
    public BikeSpot(int spotNumber)
    {
        super(spotNumber, SpotType.BIKE);
    }

    @Override 
    public boolean canFitVehicle( Vehicle vehicle)
    {
        if( vehicle.getvehicleType() == VehicleType.BIKE)
        {
            return true ;
        }
        else 
        {
            return false;
        }
    }
}

class CarSpot extends ParkingSpot
{
    public CarSpot(int spotNumber)
    {
        super(spotNumber, SpotType.CAR);
    }

    @Override 
    public boolean canFitVehicle( Vehicle vehicle)
    {
        if( vehicle.getvehicleType() == VehicleType.CAR)
        {
            return true ;
        }
        else 
        {
            return false;
        }
    }


}

class TruckSpot extends ParkingSpot
{
    public TruckSpot(int spotNumber)
    {
        super(spotNumber, SpotType.TRUCK);
    }

    @Override 
    public boolean canFitVehicle( Vehicle vehicle)
    {
        if( vehicle.getvehicleType() == VehicleType.TRUCK)
        {
            return true ;
        }
        else 
        {
            return false;
        }
    }


}


////////////////////////////////////////////
/// 
///  Step 5 : Parking Observer Class 
///  it is used to automatically update Display board when 
///  The parking Availability changes 
///   
///  Concepts : Observer Design Pattern, Interface 
/// 
/// ///////////////////////////////////////


interface ParkingObserver 
{
    void update();



}

////////////////////////////////////////////
/// 
///  Step 6 : ParkingFloor Class 
///  it is used to Manage Parking floor 
///  
///   
///  Concepts : Composition, Arraylist, Object Management 
/// 
/// ///////////////////////////////////////



class ParkingFloor
{
    //  Unique Floor Number 

    private int floorNumber;

    // Collection of all Parking Spots 

    private List <ParkingSpot> parkingSpots;


    // Collection of Observers registered for floor 

    private List <ParkingObserver> observers;

    public ParkingFloor (int floorNumber)
    {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
        this.observers = new ArrayList<>();

    }

    public int getfloorNumber()
    {
        return this.floorNumber;
    }

    public void addParkingSpot(ParkingSpot spot) // Method to add spot and not vehicle 
    {
        parkingSpots.add(spot);
    }

    public  void addObserver ( ParkingObserver observer) // Method to add an observer ( eg display board + Mobile app + Website )
    {
        observers.add(observer);
    }

    private void notifyObservers()
    {
        for (ParkingObserver observer : observers)
        {
            observer.update();
        }
    }

    // Method is going to search parking spot for specific type of vehicle
    public ParkingSpot findAvailableSpot(Vehicle vehicle)
    {
        for(ParkingSpot spot : parkingSpots)
        {
            if(!spot.isOccupied()&& spot.canFitVehicle(vehicle))
            {
                return spot;
            }
        }

        return null;
    }

    // Method called when new vehicle gets parked 

    public void occupySpot(ParkingSpot spot , Vehicle vehicle)
    {
        // Allocate spot for the vehicle 
        spot.parkVehicle(vehicle);

        // Notify all observers about the availability of spots 
        notifyObservers(); // You cannot miss this line 

    }

    public void releasespot(ParkingSpot spot)
    {

        // Release the already allocated spot
        spot.removeVehicle();

        // Notify all observers about the availability of spots 

        notifyObservers();

    }

    public int getAvailableCount(SpotType type)
    {
        int count =0;
        for (ParkingSpot spot : parkingSpots)
        {
            if(spot.getsSpotType() == type  && !spot.isOccupied())
            {
                count++;
            }
        }

        return count;
    }


    // Display all parking spots on specific floor 
    public void displayFloor()
    {
        System.out.println();

        System.out.println("Floor : "+ floorNumber);
        for(ParkingSpot spot : parkingSpots)
        {
            spot.display();
        }
    }
 
}



////////////////////////////////////////////
/// 
///  Step 7 : Create Parking DisplayBoard class 
///  Used To Create a class which displays
///  parking status 
/// 
/// Subject -> Parking Floor 
/// Observer -> Parking Display Board 
/// 
/// Note : Any observer is going to observe the subject 
/// There will be multiple observers for one subject
/// 
/// Concepts : Observer Design Patterns
/// 
/// ///////////////////////////////////////


class Parkingdisplayboard implements ParkingObserver
{
    // Floor whose Availability is displayed by this boaard 
    private ParkingFloor floor ;

    public Parkingdisplayboard (ParkingFloor floor)
    {
        this.floor = floor;
    }

    // Automatically Called whenver floor availability changes 
    @Override 
    public void update()
    {
        System.out.println();
        System.out.println("----------- Display Board --------------");
        System.out.println(" Floor : "+ floor.getfloorNumber());
        System.out.println(" Available Bike Spots : "+ floor.getAvailableCount(SpotType.BIKE));
        System.out.println(" Available Car Spots : "+ floor.getAvailableCount(SpotType.CAR));
        System.out.println(" Available Truck Spots : "+ floor.getAvailableCount(SpotType.TRUCK));
        System.out.println("----------- Display Board --------------");

        System.out.println();


    }

}

// We can create new observers for the same subject 

/*

class Parking webstie implements Parkingobserver 
{

    public void update ()
    {

    
    }
}

*/



////////////////////////////////////////////
/// 
///  Step 8 : Create Parking Strategy Class
/// 
///  Used To Create a class Parking Strategy 
///  Which is able to decide the parking spot selection 
/// 
/// Concepts : Strategy Desing Pattern 
/// 
/// ///////////////////////////////////////

// Defines  a common concept for parking spot selection algorithm
interface ParkingStrategy 
{
    // Everything in interface is by default public 
    // Interface does not contain the body of method 
    ParkingSpot findspot (List <ParkingFloor> floors, Vehicle vehicle ); // Abstract method 
}

// Selects The first available parking spot 
class FirstAvailableParkingStrategy implements ParkingStrategy
{

    @Override 

    public ParkingSpot findspot (List <ParkingFloor> floors, Vehicle vehicle )
    {
        // Iterate over all available floors 
        for(ParkingFloor floor : floors)
        {
            ParkingSpot spot = floor.findAvailableSpot(vehicle);

            if (spot  != null)
            {
                return spot;
            }
        }


        return null;
    }



}


////////////////////////////////////////////
/// 
///  Step 9 : Create Pricing Strategy Class
/// 
///  Used To Create a class Pricing Strategy 
///  Pricing Algorithm Independent of Exit logic
/// 
/// Concepts : Strategy Desing Pattern 
/// 
/// ///////////////////////////////////////


interface PricingStrategy 
{
    double CalculatePrice(Vehicle vehicle, long hourse);
}

class NormalPricingStratey implements PricingStrategy
{
    @Override 

    public  double CalculatePrice(Vehicle vehicle, long hours)
    {
        if(hours <=0 )
        {
            hours = 1;
        }

        switch (vehicle.getvehicleType()) {
            case BIKE:
                return hours*20;
            case CAR:
                
                return hours * 50;

            case TRUCK:
                
                return hours * 100;
        
            default:
                return 0;
        }
    }
}


class WeekendPricingStratey implements PricingStrategy
{
    @Override 

    public  double CalculatePrice(Vehicle vehicle, long hours)
    {
        if(hours <=0 )
        {
            hours = 1;
        }

        switch (vehicle.getvehicleType()) {
            case BIKE:
                return hours*40;
            case CAR:
                
                return hours * 100;

            case TRUCK:
                
                return hours * 200;
        
            default:
                return 0;
        }
    }
}


////////////////////////////////////////////
/// 
///  Step 10 : Create Payement Strategy Class
/// 
///  Used To Create a class Pricing Strategy 
///  It supports different types of payement methods
/// 
///  Concepts : Strategy Desing Pattern 
/// 
/// ///////////////////////////////////////


// Common contract for all payement methods 
interface PayementStrategy
{
    void pay(double amount);
}

class UPIPayement implements PayementStrategy{
    @Override 

    public void pay(double amount)
    {
        System.out.println("UPI Payement successful : Rs. "+amount);
    }
}

class CardPayement implements PayementStrategy{
    @Override 

    public void pay(double amount)
    {
        System.out.println("Card Payement successful : Rs. "+amount);
    }
}

class CashPayement implements PayementStrategy{
    @Override 

    public void pay(double amount)
    {
        System.out.println("Cash Payement successful : Rs. "+amount);
    }
}


////////////////////////////////////////////
/// 
///  Step 11 : Create Parking Ticket Class
/// 
///  Used To Create a class Parking  Ticket  
///  It represent one complete parking transaction
/// 
///  Concepts : Builder  Desing Pattern 
/// 
/// ///////////////////////////////////////

class ParkingTicket
{
    // Used for generating unique tickets 
    private static int counter = 1000;

    // Unique Ticket Number 
    private int ticketNumber;

    // Vehicle Associated with that ticekt 
    private Vehicle vehicle ;

    // Floor on which vehicle is parked 
    private ParkingFloor floor;

    // Actual spt on which vehicle is parked 
    private ParkingSpot spot;

    // Time at which vehicle Arrives 

    private LocalDateTime entryTime;

    // Time at which vehicle Exits 

    private LocalDateTime exitTime;

    // It maintains the status of ticket
    private TicketStatus status;


    public ParkingTicket(Vehicle vehicle,
                     ParkingFloor floor,
                     ParkingSpot spot)
    {
        this.ticketNumber = ++counter;
        this.vehicle = vehicle;
        this.floor = floor;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.status = TicketStatus.ACTIVE;
    }

    // Getter Method for Ticket Number 
    public int getTicketNumber()
    {
        return this.ticketNumber;
    }

    // Getter Method for for Vehicle 

    public Vehicle  getVehicle()
    {
        return this.vehicle;
    }

    // Getter Method for floor 

    public ParkingFloor getFloor()
    {
        return this.floor;
    }

    // Getter Method for Spot 

    public ParkingSpot getSpot()
    {
        return this.spot;
    }

    // Getter Method for Entrytime 

    public LocalDateTime getEntryTime()
    {
        return this.entryTime;
    }

    // Getter Method for Exit Time 

    public LocalDateTime getExittTime()
    {
        return this.exitTime;
    }

    // Getter Method for Ticket Status 

    public TicketStatus gTicketStatus()
    {
        return this.status;
    }


    // Method Gets called when Vehicle is going out 

    public void closeTicket()
    {
        this.exitTime = LocalDateTime.now();
        this.status = TicketStatus.CLOSED;
    }

    public long calculateHours()
    {
        LocalDateTime endtime;

        if(exitTime == null)
        {
            endtime = LocalDateTime.now();
        
        }

        else
        {
            endtime = exitTime;
        }

        // Calculates Actual time 
        long minutes = Duration.between(entryTime, endtime).toMinutes();

        // Converts Minutes to Hours 

        long hours = minutes/60;

        if(minutes%60 != 0)
        {
            hours++;
        }

        if(hours ==0)
        {
            hours =1;
        }

        return hours;

        
    }

    // It will display ticket on screen 
    public void displayticket()
    {
        System.out.println();
        System.out.println("-----------------------------------------");
        System.out.println("----------- Parking Ticket --------------");
        System.out.println("-----------------------------------------");
        System.out.println(" Ticekt Number : "+this.ticketNumber);
        System.out.println(" Vehicle Number : "+this.vehicle.getvehicleNumber());
        System.out.println(" Vehicle Type : "+ this.vehicle.getvehicleType());
        System.out.println(" Floor Number : "+ this.floor.getfloorNumber());
        System.out.println(" Spot Number : "+ this.spot.getSpotNumber());
        System.out.println(" Entry time : "+ this.entryTime);
        System.out.println(" Ticekt Status : "+ this.status);
        System.out.println();



    }





}


////////////////////////////////////////////
/// 
///  Step 12 : Create Entry Gate  Class
/// 
///  Used To Create a class Entry  Gate   
///  Handle Entry of vehicle and its ticket generation 
/// 
///  
/// 
/// ///////////////////////////////////////


class Entrygate
{
    private int gateNumber;

    public Entrygate(int gateNumber)
    {
        this.gateNumber = gateNumber;
    }

    public int getGateNumber ()
    {
        return this.gateNumber;
    }

    // It Generates new ticekt when vehicle enters 

    public ParkingTicket generateTicket(Vehicle vehicle, ParkingFloor floor, ParkingSpot spot)
    {
        System.out.println("Vehicle Entering from Gate : "+ this.gateNumber);

        // New Parking Ticket Gets Generated for Vehicle 
        return new ParkingTicket(vehicle,floor,spot);
    }
}

////////////////////////////////////////////
/// 
///  Step 13 : Create Exit Gate  Class
/// 
///  Used To Create a class Exit  Gate   
///  Handle Exit of vehicle and its  Billing 
///  and payement 
///  
/// 
/// ///////////////////////////////////////


class ExitGate
{
    private int gateNumber;
    public ExitGate(int gateNumber)
    {
        this.gateNumber = gateNumber;

    }

    public int getGateNumber()
    {
        return this.gateNumber;
    }

    // This performs complete Exit Operations 

    public void processExit(ParkingTicket ticket, PricingStrategy pricingStrategy, PayementStrategy payementStrategy)
    {

        // Step 1 : Close the Ticket and record the exit duration 
        ticket.closeTicket();

        // Step 2 : Calculate Duration 

        long hours = ticket.calculateHours();

        // step 3 : Calculate Parking Charfes 

        double amount = pricingStrategy.CalculatePrice(ticket.getVehicle(), hours);


        System.out.println();

        System.out.println("Vehicle Exiting From gate : " + gateNumber);

        System.out.println("Parking Duration : "+ hours);

        System.out.println("Parking Charges : "+amount);



        // Step 4 : Process The payement using selected payement stratey 

        payementStrategy.pay(amount);
        
    }
}


////////////////////////////////////////////
/// 
///  Step 14 : Create ParkingLot Class
/// 
///   Used to Create Parking Lot class 
/// 
///  Concept : Singleton Pattenr 
/// 
///  This class is the main controler of parking system 
/// 
/// ///////////////////////////////////////

// Singleton Class 
class Parkinglot
{
    // Instance of class 

    private static Parkinglot instance ;

    // Store the parkinglot name 

    private String parkingLotName;

    // Store All Floors of the ParkingLot 

    private List <ParkingFloor> floors;

    // Maps The Ticket Number with Active Parking slot 

    private Map <Integer, ParkingTicket> activetickets;

    // Maps Vehicle Number with Activ tickets 
    // Used for searching Vehicle 
    // It prevents duplicate parking 


    private Map <String,ParkingTicket> vehicleticketMap;

    // Algorrithm used for selecting parking spot 
    private ParkingStrategy parkingStrategy;

    // Algorithm used for calculating parking charges 

    private PricingStrategy pricingStrategy;

    // Private Constructor for parking lot  Class 

    private Parkinglot()
    {
       
        floors = new ArrayList<>();

        activetickets = new HashMap<>();

        vehicleticketMap = new HashMap<>();


        // Default Parking Strategy 

        parkingStrategy = new FirstAvailableParkingStrategy();

        // Default Pricing Strategy 

        pricingStrategy = new NormalPricingStratey();
    }



    // Method to return the singleton class object 

    public static synchronized Parkinglot getInstace()
    {
        if(instance == null)
        {
            instance = new Parkinglot();
        }

        return instance;
    }



     // Used to set name for complete parking lot 

     public void setParkingLotName(String parkingLotName)
     {
        this.parkingLotName = parkingLotName;
     }

     // Used to add new parking floor 
     public void addFloor(ParkingFloor floor)
     {
        // Insert In arraylist 
        floors.add(floor);
     }


     public List<ParkingFloor> getFloor()
     {
        return floors;
     }


     // This Method can be used to change the default parking strategy 

     public void setParkingStrategy (ParkingStrategy strategy)
     {
        this.parkingStrategy = strategy;
     }

     // This method is used to change the default pricing Strategy 

     public void setPricingStrategy(PricingStrategy strategy )
     {
        this.pricingStrategy = strategy;
     }



     /*
     

        Algorithm for Parking the vehicle 


        Check Duplicate Vehicle 
                  |
        Find Available Spot 
                  |
        Identify Floor for Vehicle 
                  |
        Occupy Spot for Vehicle 
                  |
        Generate Ticket for Vehicle 
                  | 
        Store the final Ticket 
        
     
     
     
     */

     public ParkingTicket parkvehicle(

                                            Vehicle vehicle,
                                            Entrygate entrygate
                                            
                                    )
     {


        // Step 1 : Prevent The Same vehicle from being parked multiple times 

        if(vehicleticketMap.containsKey(vehicle.getvehicleNumber()))
        {
            System.out.println("This Vehicle is already parked ");

            throw new RuntimeException("This Vehicle is already parked ");
        }


        // Step 2 : Find the Available spot 

        ParkingSpot spot = parkingStrategy.findspot(floors, vehicle);


        // If there is no empty spot 
        if (spot == null)
        {
            throw new RuntimeException("Parking is full ");
        }

        // Step 3 : Identify the exact floor for vehicle 

        ParkingFloor selectedFloor = null;

        for(ParkingFloor floor : floors)
        {
            ParkingSpot temp = floor.findAvailableSpot(vehicle);

            if(temp == spot)
            {
                selectedFloor = floor;
                break;
            }

        }

        if (selectedFloor == null)
        {
            throw new RuntimeException("Unable to Identify Floor ");
        }

        

        // Step 4 : Occupy Spot 

        selectedFloor.occupySpot(spot, vehicle);

        // Step 5 : Generate Parking Ticket from EntryGate 

        ParkingTicket ticket = entrygate.generateTicket(vehicle, selectedFloor, spot);

        // Step 6 : Store the ticekt using ticketnumber 

        activetickets.put(ticket.getTicketNumber(), ticket);


        // Step 7 : Store Ticekt Using Vehicle Number 

        vehicleticketMap.put(vehicle.getvehicleNumber(), ticket);


        return ticket;


     }

     /*
        Find Ticket 
            |
        Process Exit 
            |
        Payement 
            | 
        Release Spot 
            |
        Remove Active records 
     
     
     
     
     */


     public void removeVehicle  (
                                        int ticketNumber,
                                        ExitGate exitGate,
                                        PayementStrategy payementStrategy

                                )
     {


         // step 1 : Find active Ticekt  using ticket number 

         ParkingTicket ticket = activetickets.get(ticketNumber);

         if(ticket==null)
         {
            throw new RuntimeException("There is no such ticekt ");
         }

         // Step 2 : Perform Billing and payement 

         exitGate.processExit(ticket, pricingStrategy, payementStrategy);

         // Step 3 : Release the occupied spot 

         ticket.getFloor().releasespot(ticket.getSpot());

         // Step 4 : Remoce ticket 

         activetickets.remove(ticketNumber);

         // Step 5 : Remove vehicle from active vehicle 

         vehicleticketMap.remove(ticket.getVehicle().getClass());


         System.out.println(" Vehicle Removed Succesfully ");

     }

     // Search the specified method 

     public ParkingTicket searchVehicle(String vehicleNumber)
     {
        return vehicleticketMap.get(vehicleNumber);
     }

     // Display complete Parking lot information 

     public void displayParkingLot()
     {
        System.out.println();
        System.out.println("---------------------------------------");
        System.out.println(" -------- Parking Lot Details ----------");
        System.out.println("---------------------------------------");

        for(ParkingFloor floor : floors)
        {
            floor.displayFloor();
        }
     }


} // End of Parking lot class 



///////////////////////////////////////////
/// 
/// Step 15:  Controller of Project 
///  
/// ////////////////////////////////////////


/*

Step 1 : Create Parking Lot class object 

Step 2 : Create Floors 

Step 3 : Add Parking Spots 

Step 4 : Display Board 

Step 5 : Register Observers 

Step 6 : Add Floor to parkinglot 

Step 7 : Create Entry Exit Gates 

Step 8 : Display Menu 


*/


class program1017
{
    public static void main(String[] args) throws Exception
    
    {
    
        Scanner sobj = new Scanner(System.in);


        ////////////////////////////////////
        /// 
        /// 1:  Create Single Parking Lot Object 
        /// 
        /// //////////////////////////////////
        

        Parkinglot parkinglot = Parkinglot.getInstace();

        parkinglot.setParkingLotName("Marvellous ParkEngine");


        ////////////////////////////////////
        /// 
        /// 2 & 3 : Creat Multiple FLoor and Spots 
        /// 
        /// //////////////////////////////////
        
    
        // ADD FIRST FLOOR 


        ParkingFloor floor1 = new ParkingFloor(1);

        floor1.addParkingSpot(new BikeSpot(101));
        floor1.addParkingSpot(new BikeSpot(102));

        floor1.addParkingSpot(new CarSpot(103));
        floor1.addParkingSpot(new CarSpot(104));

        floor1.addParkingSpot(new TruckSpot(105));
        floor1.addParkingSpot(new TruckSpot(106));


        ////////////////////////////////////
        /// 
        /// 4: Create Display Board 
        /// 
        /// //////////////////////////////////



        Parkingdisplayboard board1 = new Parkingdisplayboard(floor1);

        // Register the Display Board with Observer 

        floor1.addObserver(board1);


        // ADD SECOND FLOOR 

        ParkingFloor floor2 = new ParkingFloor(2);

        floor2.addParkingSpot(new BikeSpot(201));
        floor2.addParkingSpot(new BikeSpot(202));

        floor2.addParkingSpot(new CarSpot(203));
        floor2.addParkingSpot(new CarSpot(204));

        floor2.addParkingSpot(new TruckSpot(205));
        floor2.addParkingSpot(new TruckSpot(206));


        ////////////////////////////////////
        /// 
        /// 5: Create Display Board 
        /// 
        /// //////////////////////////////////



        Parkingdisplayboard board2 = new Parkingdisplayboard(floor2);

        // Register the Display Board with Observer 

        floor2.addObserver(board2);


        ////////////////////////////////////
        /// 
        /// 6: Add floor to parking lot 
        /// 
        /// //////////////////////////////////
        

        parkinglot.addFloor(floor1);
        parkinglot.addFloor(floor2);


        ////////////////////////////////////
        /// 
        /// 7: Create EntryGate and Exit Gate 
        /// 
        /// //////////////////////////////////



        Entrygate entrygate = new Entrygate(1);
        ExitGate exitGate = new ExitGate(1);


        /////////////////////////////////////
        /// 
        /// 8: Display Menu 
        /// 
        /// //////////////////////////////////


        int choice = 0;

        while (true)
        {
            System.out.println("-------------------------------------");
            System.out.println("------ Marvellous Park Engine -------");
            System.out.println("-------------------------------------");


            System.out.println("1 : Park Vehicle ");
            System.out.println("2 : Exit Vehicle ");
            System.out.println("3 : Search Vehicle ");
            System.out.println("4 : Display Parking Lot ");
            System.out.println("5 : Exit");

            System.out.println();
            System.out.println();

            System.out.println("Enter Your Choice : ");

            choice = sobj.nextInt();

            try
            {

                switch (choice) {
                    case 1: // Park Vehicle 
                        
                        {
                            System.out.println();

                            System.out.println(" Select Vehicle Type : ");

                            System.out.println("1 : BIKE");
                            System.out.println("2 : CAR");
                            System.out.println("3 : TRUCK");

                            int type = sobj.nextInt();

                            System.out.println("Enter Vehicle Number ");
                            String number = sobj.next();

                            Vehicle vehicle;

                            // Factory Pattern is used 

                            switch (type) {
                                case 1: // Bike 
                                    vehicle = VehicleFactory.creatVehicle(VehicleType.BIKE, number);
                                    break;
                                case 2: // Car 
                                    vehicle = VehicleFactory.creatVehicle(VehicleType.CAR, number);
                                    break;
                                case 3:
                                    vehicle = VehicleFactory.creatVehicle(VehicleType.TRUCK, number);
                                    break;
                            
                                default: // Truck 
                                    System.out.println("Invalid Vehicle Type ");
                                    continue;
                            }

                            // Park the Vehicle and Generate the Ticket 

                            ParkingTicket ticket = parkinglot.parkvehicle(vehicle, entrygate);

                            // Display Generated Ticket 

                            ticket.displayticket();

                            break;

                        }// End of Case 1 (Park Vehicle )
                
                    case 2: // Exit Vehicle 
                        
                        {
                            System.out.println("Enter Ticket Number : ");

                            int ticketNumber = sobj.nextInt();

                            System.out.println();

                            System.out.println(" Enter the Payement option ");

                            System.out.println(" 1 : Cash ");
                            System.out.println(" 2 : UPI ");
                            System.out.println(" 3 : Card");


                            int payementType = sobj.nextInt();

                            PayementStrategy payementStrategy;

                            switch (payementType) 
                            {
                                case 1: // Cash 
                                    payementStrategy = new CashPayement();
                                    break;
                                case 2: // UPI 
                                    payementStrategy = new UPIPayement();
                                    break;
                                case 3: // Card 
                                    payementStrategy = new CardPayement();
                                    break;
                            
                                default: 
                                    System.out.println("Invalid Payement Option ");
                                    continue;

                                
                            } // End of Payement Switch 

                            parkinglot.removeVehicle(ticketNumber, exitGate, payementStrategy);

                            break;

                        } // End of Case 2 ( Exit vehicle )

                    case 3: // Search Vehicle 
                        
                        {
                            System.out.println(" Enter Vehicle Number");

                            String vehiclenumber = sobj.next();

                            ParkingTicket ticket = parkinglot.searchVehicle(vehiclenumber);

                            if(ticket == null )
                            {
                                System.out.println("This Vehicle is not parked ");
                            }
                            else
                            {
                                ticket.displayticket();
                            }

                            break;


                        }

                    case 4: // Display ParkingLot 
                        
                        {
                            parkinglot.displayParkingLot();

                            break;
                        }

                    case 5:
                        
                        {
                            System.out.println("Thank you for using Marvellous Park Engine ");
                            
                            sobj.close();

                            return ;
                        }



                    default:
                        System.out.println("Invalid Option ");
                        
                } // End of switch 
            } // End of Try 

            catch( Exception e)
            {
                System.out.println("Exception Occured : "+ e);
            }


        }// End of While 


        

    }// End of Main 
}// End of main class 