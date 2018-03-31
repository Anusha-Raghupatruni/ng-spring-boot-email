import { Mapper } from './mapper';
import { Person } from './person';

export class Email implements Mapper<Email>{
  constructor(
    private sender: Person = null,
    private recipients: Person[] = null,
    private content: string = null,
    private ccs?: Person[],
    private bccs?: Person[],
    private subject?: string) {}

  map(input: any): Email {
    let sender: Person;
    let recipients: Person[];
    let ccs: Person[];
    let bccs: Person[];
    let subject: string;
    let content: string;

    if (input.sender && input.sender[0]) {
      sender = input.sender[0];
    } else {
      throw new Error('Sender field is required');
    }

    if (input.recipients && input.recipients[0]) {
      recipients = input.recipients;
    } else {
      throw new Error('Recipients are required');
    }

    if (input.ccs) {
      ccs = input.ccs;
    }

    if (input.bccs) {
      bccs = input.bccs;
    }

    if (input.subject) {
      subject = input.subject.trim();
    }

    if (input.content) {
      content = input.content.trim();
    } else {
      throw new Error('Email content is required');
    }

    return new Email(sender, recipients, content, ccs, bccs, subject);
  }
}
