This project is a shared whiteboard that allows multiple users to draw simultaneously on a canvas.

This shared whiteboard supports a range of features such as freehand drawing with the mouse, drawing lines and shapes such as circles and
rectangles and inserting text. In addition to theses features, this implementation also has a chat window which allows all the current
users of the system to broadcast messages to each other.

To run these files, please do the following steps:


1. java -jar Server.jar port_number

2. java -jar Manager.jar ip_address port_number

3. java -jar User.jar ip_address port_number


For instance: 
   java -jar Server.jar 6666

   java -jar Manager.jar "127.0.0.1" 6666

   java -jar User.jar "127.0.0.1" 6666
