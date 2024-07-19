const fs = require('fs');
const path = require('path');
const config = require('./config.json');

const indexPath = path.join(__dirname, 'public', 'index.html');
let indexHtml = fs.readFileSync(indexPath, 'utf8');

indexHtml = indexHtml.replace(/%APP_NAME%/g, config.app);

fs.writeFileSync(indexPath, indexHtml, 'utf8');

