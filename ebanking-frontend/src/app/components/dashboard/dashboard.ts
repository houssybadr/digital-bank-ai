import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Chart, registerables } from 'chart.js';
import { environment } from '../../../environments/environment';

Chart.register(...registerables);

interface DashboardStats {
  totalCustomers: number;
  totalAccounts: number;
  totalOperations: number;
  totalDebitOperations: number;
  totalCreditOperations: number;
  totalDebitAmount: number;
  totalCreditAmount: number;
  currentAccountCount: number;
  savingAccountCount: number;
}

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('opsChart') opsChartRef!: ElementRef;
  @ViewChild('accountsChart') accountsChartRef!: ElementRef;
  @ViewChild('amountsChart') amountsChartRef!: ElementRef;

  stats!: DashboardStats;
  loading = true;

  private opsChart?: Chart;
  private accountsChart?: Chart;
  private amountsChart?: Chart;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<DashboardStats>(`${environment.backendHost}/api/dashboard/stats`).subscribe({
      next: data => {
        this.stats = data;
        this.loading = false;
        setTimeout(() => this.buildCharts(), 100);
      },
      error: () => this.loading = false
    });
  }

  ngAfterViewInit(): void {}

  buildCharts(): void {
    if (!this.stats) return;

    this.opsChart = new Chart(this.opsChartRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Débits', 'Crédits'],
        datasets: [{
          data: [this.stats.totalDebitOperations, this.stats.totalCreditOperations],
          backgroundColor: ['#dc3545', '#198754'],
          borderWidth: 2
        }]
      },
      options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
    });

    this.accountsChart = new Chart(this.accountsChartRef.nativeElement, {
      type: 'pie',
      data: {
        labels: ['Comptes Courants', 'Comptes Épargne'],
        datasets: [{
          data: [this.stats.currentAccountCount, this.stats.savingAccountCount],
          backgroundColor: ['#0d6efd', '#20c997'],
          borderWidth: 2
        }]
      },
      options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
    });

    this.amountsChart = new Chart(this.amountsChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Montant Débits (MAD)', 'Montant Crédits (MAD)'],
        datasets: [{
          label: 'Montants',
          data: [this.stats.totalDebitAmount, this.stats.totalCreditAmount],
          backgroundColor: ['rgba(220,53,69,0.7)', 'rgba(25,135,84,0.7)'],
          borderColor: ['#dc3545', '#198754'],
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    });
  }
}
