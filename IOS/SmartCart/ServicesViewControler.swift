//
//  ServicesViewControler.swift
//  SmartCart
//
//  Created by Luis Menezes on 05/01/16.
//  Copyright © 2016 Pedro Abade. All rights reserved.
//

import UIKit
import Parse
import CoreData

class ServicesViewControler: UIViewController {
    @IBOutlet weak var label_1: UILabel!
    @IBOutlet weak var counter_11: UILabel!
    @IBOutlet weak var counter_12: UILabel!
    @IBOutlet weak var label_2: UILabel!
    @IBOutlet weak var counter_21: UILabel!
    @IBOutlet weak var counter_22: UILabel!
    @IBOutlet weak var label_3: UILabel!
    @IBOutlet weak var counter_31: UILabel!
    @IBOutlet weak var counter_32: UILabel!
    @IBOutlet weak var label_4: UILabel!
    @IBOutlet weak var counter_41: UILabel!
    @IBOutlet weak var counter_42: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        RefreshServices()
    }
    
    @IBAction func button1Down(sender: AnyObject) {
        label_1.text = "Minha"
    }
    
    @IBAction func button2Down(sender: AnyObject) {
        label_2.text = "Minha"
    }
    
    @IBAction func button3Down(sender: AnyObject) {
        label_3.text = "Minha"
    }
    
    @IBAction func button4Down(sender: AnyObject) {
        label_4.text = "Minha"
    }
    
    func RefreshServices() {
        let query = PFQuery(className:"Services")
        query.findObjectsInBackgroundWithBlock {
            (objects: [PFObject]?, error: NSError?) -> Void in
            
            if error == nil {
                
                print("Successfully retrieved \(objects!.count) services.")
                if objects!.count != 0 && objects!.count != 4{
                    print(objects![0]["availableNumber"] as! String)
                    
                    self.counter_11.text = objects![0]["availableNumber"] as! String
                    self.counter_12.text = objects![0]["currentNumber"] as! String
                    self.counter_21.text = objects![1]["availableNumber"] as! String
                    self.counter_22.text = objects![1]["currentNumber"] as! String
                    self.counter_31.text = objects![2]["availableNumber"] as! String
                    self.counter_32.text = objects![2]["currentNumber"] as! String
                    self.counter_41.text = objects![3]["availableNumber"] as! String
                    self.counter_42.text = objects![3]["currentNumber"] as! String
                 }
            } else {
                // Log details of the failure
                print("Error retrieving data from db!")
            }
        }
    }
    
    func saveLogin(id: Int16 , senha : Int16) {
        
        // create an instance of our managedObjectContext
        let moc = DataController().managedObjectContext
        
        // we set up our entity by selecting the entity and context that we're targeting
        let entity = NSEntityDescription.insertNewObjectForEntityForName("Services", inManagedObjectContext: moc) as! Services
        // add our data
        /*
        switch id{
            
        //case 1:entity.setValue(senha, forKey: "n1")
        }*/
        
        
        // we save our entity
        do {
            try moc.save()
        } catch {
            fatalError("Failure to save context: \(error)")
        }
    }

}
