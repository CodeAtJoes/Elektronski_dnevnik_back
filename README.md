# OnClickListenerExample
Few example of the implementation

### Option 1

```java
Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

            }
        });
```
### Option 2
```bash
cd git
docker build -t joemccann/dillinger:${package.json.version}
```

Dillinger is a cloud-enabled, mobile-ready, offline-storage, AngularJS powered HTML5 Markdown editor.

  - Type some Markdown on the left
  - See HTML in the right
  - Magic
  
 Markdown is a lightweight markup language based on the formatting conventions that people naturally use in email.

> The overriding design goal for Markdown's
> formatting syntax is to make it as readable
> as possible. The idea is that a
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

### Tech

Dillinger uses a number of open source projects to work properly:

* [AngularJS] - HTML enhanced for web apps!
* [Ace Editor] - awesome web-based text editor
* [markdown-it] - Markdown parser done right. Fast and easy to extend.
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [node.js] - evented I/O for the backend
* [Express] - fast node.js network app framework [@tjholowaychuk]
* [Gulp] - the streaming build system
* [Breakdance](http://breakdance.io) - HTML to Markdown converter
* [jQuery] - duh

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

### Installation

Dillinger requires [Node.js](https://nodejs.org/) v4+ to run.

Install the dependencies and devDependencies and start the server.

```sh
$ cd dillinger
$ npm install -d
$ node app
```

For production environments...

```sh
$ npm install --production
$ NODE_ENV=production node app
```

### Plugins

Dillinger is currently extended with the following plugins. Instructions on how to use them in your own application are linked below.

| Plugin | README |
| ------ | ------ |
| Dropbox | [plugins/dropbox/README.md][PlDb] |
| Github | [plugins/github/README.md][PlGh] |
| Google Drive | [plugins/googledrive/README.md][PlGd] |
| OneDrive | [plugins/onedrive/README.md][PlOd] |
| Medium | [plugins/medium/README.md][PlMe] |
| Google Analytics | [plugins/googleanalytics/README.md][PlGa] |


<table>
<tr>
<th>ID</th><th>Name</th><th>Rank</th>
</tr>
<tr>
<td>1</td><td>Tom Preston-Werner</td><td>Awesome</td>
</tr>
<tr>
<td>2</td><td>Albert Einstein</td><td>Nearly as awesome</td>
</tr>
</table>



## Special Thanks
* [CodeMirror](http://codemirror.net/).
* [Github](http://developer.github.com/) for API and style.
* [marked](https://github.com/chjj/marked).
* [highlight.js](http://softwaremaniacs.org/soft/highlight/en/).
* [Twitter Bootstrap](http://twitter.github.com/bootstrap/) with [Font Awesome](http://fortawesome.github.com/Font-Awesome/).
* [HTML5 ★ BOILERPLATE](http://html5boilerplate.com/).
* [jQuery](http://jquery.com/).
* [HTML5 ROCKS](http://www.html5rocks.com/en/tutorials/file/xhr2/) for usage of BLOB.
* [balupton](https://github.com/balupton).

## Licence

Source code can be found on [github](https://github.com/georgeOsdDev/markdown-edit), licenced under [MIT](http://opensource.org/licenses/mit-license.php).

Developed by [Danijel.Copic](http://adit.rs)
