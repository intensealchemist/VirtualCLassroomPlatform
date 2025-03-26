const canvas = document.getElementById('whiteboardCanvas');
const context = canvas.getContext('2d');
const toolSelector = document.getElementById('toolSelector');
const sizeSelector = document.getElementById('sizeSelector');
const sizeValue = document.getElementById('sizeValue');
const colorSelector = document.getElementById('colorSelector');
const brushSelector = document.getElementById('brushSelector');
const undoButton = document.getElementById('undoButton');
const redoButton = document.getElementById('redoButton');
const textInputButton = document.getElementById('textInputButton');
const textSizeSelector = document.getElementById('textSizeSelector');
const textSizeValue = document.getElementById('textSizeValue');
const textInputField = document.getElementById('textInputField');
const submitTextButton = document.getElementById('submitText');
const textInput = document.getElementById('textInput');



let isDrawing = false;
let drawingHistory = [];
let currentHistoryIndex = -1;
let isTextInputMode = false;
let textInputPosition = { x: 0, y: 0 };

saveCanvasState();

canvas.addEventListener('mousedown', (e) => {
  if (isTextInputMode) return;
  isDrawing = true;
  if (toolSelector.value === 'pen') {
    context.beginPath();
  } else if (toolSelector.value === 'text') {
    textInputPosition = { x: e.clientX - canvas.getBoundingClientRect().left, y: e.clientY - canvas.getBoundingClientRect().top };
    const text = prompt('Enter text:');
    if (text !== null && text !== '') {
      drawText(text, textInputPosition);
      saveCanvasState();
    }
  } else {
    draw(e);
  }
});


canvas.addEventListener('mousemove', (e) => {
  if (isDrawing && !isTextInputMode) {
    draw(e);
  }
});

canvas.addEventListener('mouseup', () => {
  if (!isTextInputMode) {
    isDrawing = false;
    if (toolSelector.value === 'pen') {
      context.closePath();
      saveCanvasState();
    }
  }
});

sizeSelector.addEventListener('input', () => {
  sizeValue.textContent = sizeSelector.value;
});

colorSelector.addEventListener('input', () => {
  context.strokeStyle = colorSelector.value;
  context.fillStyle = colorSelector.value;
});

undoButton.addEventListener('click', undo);
redoButton.addEventListener('click', redo);

textInputButton.addEventListener('click', () => {
  isTextInputMode = !isTextInputMode; 
  toolSelector.value = isTextInputMode ? 'text' : 'pen'; 
});

submitTextButton.addEventListener('click', () => {
  const text = textInput.value.trim();
  if (text !== '') {
    drawText(text, textInputPosition);
    saveCanvasState();
    textInput.value = ''; 
  }
});


textSizeSelector.addEventListener('input', () => {
  textSizeValue.textContent = textSizeSelector.value;
});

document.addEventListener('keydown', (e) => {
  if (e.ctrlKey && e.key === 'z') {
    undo();
  } else if (e.ctrlKey && e.key === 'y') {
    redo();
  }
});


function draw(e) {
  const x = e.clientX - canvas.getBoundingClientRect().left;
  const y = e.clientY - canvas.getBoundingClientRect().top;

  context.lineWidth = sizeSelector.value;

  if (brushSelector.value === 'round') {
    context.lineCap = 'round';
  } else {
    context.lineCap = 'square';
  }

  if (toolSelector.value === 'pen') {
    context.lineTo(x, y);
    context.stroke();
  } else if (toolSelector.value === 'eraser') {
    context.clearRect(x - sizeSelector.value / 2, y - sizeSelector.value / 2, sizeSelector.value, sizeSelector.value);
  }
}

function drawText(text, position) {
  context.font = `${textSizeSelector.value}px Arial`; 
  context.fillText(text, position.x, position.y); 
}

function saveCanvasState() {
  const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
  drawingHistory = drawingHistory.slice(0, currentHistoryIndex + 1);
  drawingHistory.push(imageData);
  currentHistoryIndex = drawingHistory.length - 1;
  updateUndoRedoButtons();
}

function undo() {
  if (currentHistoryIndex > 0) {
    currentHistoryIndex--;
    context.putImageData(drawingHistory[currentHistoryIndex], 0, 0);
    updateUndoRedoButtons();
  }
}

function redo() {
  if (currentHistoryIndex < drawingHistory.length - 1) {
    currentHistoryIndex++;
    context.putImageData(drawingHistory[currentHistoryIndex], 0, 0);
    updateUndoRedoButtons();
  }
}

function updateUndoRedoButtons() {
  undoButton.disabled = currentHistoryIndex === 0;
  redoButton.disabled = currentHistoryIndex === drawingHistory.length - 1;
}
