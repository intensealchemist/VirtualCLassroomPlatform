(function(){
  // Footer year
  var y = document.getElementById('year');
  if (y) y.textContent = new Date().getFullYear();

  // Chart.js simple area chart for learning activity
  var ctx = document.getElementById('activityChart');
  if (ctx && window.Chart) {
    var gradient = ctx.getContext('2d').createLinearGradient(0,0,0,180);
    gradient.addColorStop(0, 'rgba(99,102,241,0.35)');
    gradient.addColorStop(1, 'rgba(99,102,241,0.02)');

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'],
        datasets: [{
          label: 'Minutes Learned',
          data: [25, 40, 35, 60, 45, 30, 50],
          fill: true,
          backgroundColor: gradient,
          borderColor: '#6366f1',
          borderWidth: 2,
          pointRadius: 3,
          pointBackgroundColor: '#6366f1',
          tension: 0.35
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: { grid: { display: false } },
          y: { grid: { color: 'rgba(0,0,0,0.06)' }, beginAtZero: true }
        },
        plugins: { legend: { display: false } }
      }
    });
  }
})();
