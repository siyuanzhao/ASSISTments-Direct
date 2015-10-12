<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Joint Example</title>
<link rel="stylesheet" type="text/css" href="stylesheets/joint.min.css" />
<script src="js/jquery.min.js"></script>
<script src="js/lodash.min.js"></script>
<script src="js/backbone-min.js"></script>
<script src="js/joint.min.js"></script>
</head>
<body>
<div id="myholder"></div>
  <script type="text/javascript">

    var graph = new joint.dia.Graph;

    var paper = new joint.dia.Paper({
        el: $('#myholder'),
        width: 600,
        height: 200,
        model: graph,
        gridSize: 1
    });
    
    joint.shapes.basic.Rect = joint.shapes.basic.Generic.extend({
        markup: '<g class="rotatable"><g class="scalable"><rect/></g><text/></g>',

        defaults: joint.util.deepSupplement({
            type: 'basic.Rect',
            attrs: {
                'rect': { fill: 'white', stroke: 'black', 'follow-scale': true, width: 80, height: 40 },
                'text': { 'font-size': 14, 'ref-x': .5, 'ref-y': .5, ref: 'rect', 'y-alignment': 'middle', 'x-alignment': 'middle' }
            }
        }, joint.shapes.basic.Generic.prototype.defaults)
    });

    var rect = new joint.shapes.basic.Rect({
        position: { x: 100, y: 30 },
        size: { width: 100, height: 30 },
        attrs: { rect: { fill: 'blue' }, text: { text: 'my box', fill: 'white' } }
    });
    
    var rect2 = rect.clone();
    rect2.translate(300);

    var link = new joint.dia.Link({
        source: { id: rect.id },
        target: { id: rect2.id }
    });
    
    link.attr({
        '.connection': { stroke: 'blue' },
        '.marker-source': { fill: 'red', d: 'M 10 0 L 0 5 L 10 10 z' },
        '.marker-target': { fill: 'yellow', d: 'M 10 0 L 0 5 L 10 10 z' }
    });

    graph.addCells([rect, rect2, link]);

  </script>
</body>
</html>