//
//  ProductCellViewController.swift
//  SmartCart
//
//  Created by Pedro Abade on 04/01/16.
//  Copyright © 2016 Pedro Abade. All rights reserved.
//

import UIKit

class ProductCellViewController: UITableViewCell {
    @IBOutlet weak var prod_image: UIImageView!
    @IBOutlet weak var prod_name: UILabel!
    @IBOutlet weak var prod_price: UILabel!
    @IBOutlet weak var prod_total_price: UILabel!
    @IBOutlet weak var prod_desc: UILabel!
    @IBOutlet weak var prod_quant: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
