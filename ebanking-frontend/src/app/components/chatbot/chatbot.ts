import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface ChatMessage {
  role: 'user' | 'bot';
  text: string;
  time: Date;
}

@Component({
  selector: 'app-chatbot',
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.scss',
})
export class ChatbotComponent {
  messages: ChatMessage[] = [
    { role: 'bot', text: 'Bonjour ! Je suis votre assistant bancaire IA. Comment puis-je vous aider ?', time: new Date() }
  ];
  userInput = '';
  loading = false;

  constructor(private http: HttpClient) {}

  sendMessage(): void {
    const text = this.userInput.trim();
    if (!text || this.loading) return;

    this.messages.push({ role: 'user', text, time: new Date() });
    this.userInput = '';
    this.loading = true;

    this.http.post<{ response: string }>(`${environment.backendHost}/api/chat`, { message: text }).subscribe({
      next: res => {
        this.messages.push({ role: 'bot', text: res.response, time: new Date() });
        this.loading = false;
      },
      error: () => {
        this.messages.push({ role: 'bot', text: 'Désolé, une erreur s\'est produite. Vérifiez la clé OpenAI.', time: new Date() });
        this.loading = false;
      }
    });
  }

  onKeyUp(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) this.sendMessage();
  }
}
