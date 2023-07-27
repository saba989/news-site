FilterCards
=========
This component is written in HTL.

* Allows to filter cards on the  basis of Tags

## Usage

The filterCards component will display the cards sorted on basis of tags.
This Card is clickable, so it will navigate to that specific page when get clicked.
This Card image is retrieved from the Hero Image component of that page.

## Dialog has following properties
1. `./rootPagePath` - will store the Root Page Path i,e under which path we have to search for tags.
2. `./noOfItems` - will store the no of items we have to display initially and no load more.
3. `./pagination` - will store the boolean value for if pagination is required or not.
4. `./tagList` - will store the list of tags which we have to search.
5. `./imagePathReference` - will store the Image Path Reference of image which have to be display on cards i.e hero image component path.

