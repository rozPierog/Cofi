---
layout: page
permalink: /contribute/recipe.html

---
# Add new Default Recipe

Every fresh install of Cofi comes with few **default recipes** just to get users started. The first few _(V60, French Press, and Chemex)_ are taken from [James Hoffmann](https://www.youtube.com/channel/UCMb0O2CdPBNi-QqPk5T3gsQ) with his permission to do so.

Before you open a pull request with a new default recipe please **do open an issue** so that we can evaluate if that recipe can be included.

A good contender for new default recipe:

- is unique - _no two recipes for same brew method_
- is free to use - _must come with full permission to use from author_
- can be followed without high-end, expensive equipment - _Cofi isn't about intimidating new coffee lovers_
- has a source in the description

# How to add a new default recipe

## 🤖 If you are an Android developer:

Everything you need to start is [here](https://github.com/rozPierog/Cofi/blob/main/app/src/main/java/com/omelan/cofi/model/PrepopulateData.kt)

1. Add next `<recipe_name>Id` - must be one higher than one above it
2. Add Recipe object to the `recipes` list
3. Add Steps objects to the `steps` list
4. Remove the app from a device, and compile+install it again
5. Check if everything works
6. Submit a PR

## 🧑 If you are not an Android developer

1. Open an issue on GitHub with a link to the recipe
2. Make a case why you think it would be a good fit to include
3. Attach permission of the author
