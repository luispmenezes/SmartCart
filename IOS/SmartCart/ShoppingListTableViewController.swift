//
//  ShoppingListTableViewController.swift
//  SmartCart
//
//  Created by Pedro Abade on 04/01/16.
//  Copyright © 2016 Pedro Abade. All rights reserved.
//

import UIKit
import Parse

class ShoppingListTableViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet var myTable: UITableView!
    let cellIdentifier = "prod_cell"
    
    struct Products {
        let id: String
        let img: UIImage
        let name: String
        let desc: String
        let quant: String
        let price: String
        var qty: Int
        var tot_price: Double
    }
    var list:[Products] = []
    var qrCodeText:String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return list.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) ->UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! ProductCellViewController
        
        cell.prod_name.text = list[indexPath.row].name
        cell.prod_desc.text = list[indexPath.row].quant + " - " + list[indexPath.row].desc
        cell.prod_price.text = list[indexPath.row].price
        cell.prod_quant.text = "X " + String(list[indexPath.row].qty)
        let ttprice_double = list[indexPath.row].tot_price
        let ttprice_string = String(format:"%.1f", ttprice_double)
        cell.prod_total_price.text = ttprice_string + " €"
        cell.prod_image.image = list[indexPath.row].img
        return cell
    }
    
    @IBAction func scanPressed(sender: UIButton) {
        qrCodeText = ""
    }
    
    @IBAction func unwindFromScannerScanned(segue: UIStoryboardSegue) {
        if(segue.sourceViewController .isKindOfClass(ScannerViewController))
        {
            let scannerView:ScannerViewController = segue.sourceViewController as! ScannerViewController
            
            if (qrCodeText=="") {
                qrCodeText = scannerView.qrCodeValue!
                
                let query = PFQuery(className:"Product")
                query.whereKey("ID", equalTo: "\(qrCodeText)")
                query.findObjectsInBackgroundWithBlock {
                    (objects: [PFObject]?, error: NSError?) -> Void in
                    
                    if error == nil {
                        if objects!.count != 0 {
                            let object = objects![0]
                            let name = object["Name"] as! String
                            let desc = object["PPU"] as! String
                            let price = object["Price"] as! String
                            let quant = object["Quantity"] as! String
                            let thumbnail = object["thumbnail"] as! PFFile
                            
                            var c=0
                            var exist = false
                            for item in self.list {
                                if(item.id == self.qrCodeText){
                                    exist = true
                                    self.list[c].qty = self.list[c].qty + 1
                                    
                                    let myString = self.list[c].price
                                    let myFloat = (myString as NSString).doubleValue
                                    
                                    
                                    let aux_price = myFloat
                                    let aux_qty = Double(self.list[c].qty)
                                    self.list[c].tot_price = aux_qty * aux_price
                                }
                                c = c+1
                            }
                            
                            if (exist) {
                                self.myTable.reloadData()
                            } else {
                                //var wait = false
                                var imagem:UIImage?
                                thumbnail.getDataInBackgroundWithBlock{(imageData: NSData?, error: NSError?) -> Void in
                                    if error == nil {
                                        if let image = UIImage(data: imageData!) {
                                            imagem = image
                                            
                                                let myString = price
                                                let myFloat = (myString as NSString).doubleValue
                                                
                                                let prod = Products.init(id: self.qrCodeText, img: imagem!, name: name, desc: desc, quant: quant, price: price, qty: 1, tot_price: myFloat)
                                                
                                                self.list.append(prod)
                                                
                                                self.myTable.reloadData()
                                        }
                                    }
                                    
                                    
                                }
                            }
                        } else {
                            print("No product!")
                        }
                    } else {
                        // Log details of the failure
                        print("Error retrieving data from db!")
                    }
                }
                
            }
        }
    }

    func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    // Override to support editing the table view.
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        
        if editingStyle == .Delete {
            self.list.removeAtIndex(indexPath.row)
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Left)
        }
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        //self.performSegueWithIdentifier( "menu_quantity", sender: nil)
    }
}
