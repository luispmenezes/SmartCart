//
//  Login+CoreDataProperties.swift
//  
//
//  Created by Luis Menezes on 07/01/16.
//
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

import Foundation
import CoreData

extension Login {

    @NSManaged var username: String?
    @NSManaged var password: String?

}
