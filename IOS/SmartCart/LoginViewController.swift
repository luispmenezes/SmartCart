//
//  LoginViewController.swift
//  SmartCart
//
//  Created by Pedro Abade on 02/01/16.
//  Copyright © 2016 Pedro Abade. All rights reserved.
//

import UIKit
import Parse
import CoreData

class LoginViewController: UIViewController {

    @IBOutlet weak var usernameText: UITextField!
    @IBOutlet weak var passText: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchLogin()
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?){
        view.endEditing(true)
        super.touchesBegan(touches, withEvent: event)
    }
    
    @IBAction func sign_in(sender: AnyObject) {
        let username = usernameText.text! as String
        let password = passText.text! as String
        
        let query = PFQuery(className:"Users")
        query.whereKey("username", equalTo: "\(username)")
        query.findObjectsInBackgroundWithBlock {
            (objects: [PFObject]?, error: NSError?) -> Void in
            
            if error == nil {
                if objects!.count != 0 {
                    let object = objects![0]
                    let pass = object["password"] as! String
                    
                    if pass != password {
                        print("Wrong password!")
                    } else {
                        self.performSegueWithIdentifier("login_ok", sender: nil)
                        
                        print("Login OK!")
                    }
                } else {
                    print("No user with that username!")
                }
            } else {
                // Log details of the failure
                print("Error retrieving data from db!")
            }
        }
        saveLogin(username, password: password)
    }
    
    @IBAction func unwindFromRegisterCancel(segue: UIStoryboardSegue) {
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    func saveLogin(user: String , password: String) {
        
        // create an instance of our managedObjectContext
        let moc = DataController().managedObjectContext
        
        // we set up our entity by selecting the entity and context that we're targeting
        let entity = NSEntityDescription.insertNewObjectForEntityForName("Login", inManagedObjectContext: moc) as! Login
        // add our data
        entity.setValue(user, forKey: "username")
        entity.setValue(password, forKey: "password")
        
        // we save our entity
        do {
            try moc.save()
        } catch {
            fatalError("Failure to save context: \(error)")
        }
    }
    
    func fetchLogin() {
        let moc = DataController().managedObjectContext
        let loginFetch = NSFetchRequest(entityName: "Login")
        
        do {
            let fetchedLog = try moc.executeFetchRequest(loginFetch) as! [Login]
            usernameText.text = fetchedLog.last?.username
            passText.text = fetchedLog.last?.password
        } catch {
            fatalError("Failed to fetch person: \(error)")
        }
    }

}
