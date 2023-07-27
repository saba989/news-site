
Blogs
====
Blogs Component written in HTL.

## Features

* Allows addition of Blog item components of varying resource type.
* Allowed components can be configured through policy configuration.
* Blogs item include:
  * Title
  * Subtitle
  * Blog Multifield
  	* Blog Image
  	* Blog Title
    * Blog Subtitle
  	* Blog Description
    * Blog LinkUrl
    * Blog LinkTarget

* Blogs Component contains nested multifield inside into it.
* Allows linking with link inside blog and it is associated with inside link text.

### Use Object
The Blog component uses the `com.ixm.core.models.BlogsModel` Sling model as its Use-object.

### Properties Configurations
The following configuration of the multifield component are used:

1. `./title` - defines the title that need to be displayed on the blog.
2. `./subtitle` - defines the subtitle that need to be displayed on the blog.
3. `./description` - defines the description that need to be displayed on the blog.


### Multifield Configurations

1. `./blogImage` - defines the image that need to be displayed on the blog.
2. `./blogTitle` - defines the title that need to be displayed on the blog.
3. `./blogSubTitle` - defines the subtitle that need to be displayed on the blog.
4. `./blogDescription` - defines the description that need to be displayed on the blog.
5. `./logLinkUrl` - defines the link url that need to be displayed on the blog.
6. `./blogLinkTarget` - defines the link target  that need to be displayed on the blog.

