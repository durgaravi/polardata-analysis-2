from bottle import route, run, debug, template, request, static_file, error, response
import webbrowser

@route('/:filename#.*#')
def send_static(filename):
    return static_file(filename, root='resources/public')

@error(403)
def mistake403(code):
	return 'There is a mistake in your url!'

@error(404)
def mistake404(code):
	return 'Sorry, this page does not exist!'

def main_fn():
	webbrowser.open("http://localhost:8880/ner.html", new=0, autoraise=True)
	webbrowser.open("http://localhost:8880/ner2.html", new=0, autoraise=True)
	webbrowser.open("http://localhost:8880/geotopic.html", new=0, autoraise=True)
	webbrowser.open("http://localhost:8880/sweet.html", new=0, autoraise=True)
	webbrowser.open("http://localhost:8880/metadata.html", new=0, autoraise=True)
	webbrowser.open("http://localhost:8880/tree.html", new=0, autoraise=True)
	run(host="localhost", port=8880)
	
if __name__ == "__main__":
	main_fn()
	
