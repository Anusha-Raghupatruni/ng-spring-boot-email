import { Mapper } from './mapper';

export class Person implements Mapper<Person> {
  constructor(private email: string = '') {}

  map(data: any): Person {
    this.email = (data || "").trim();
    return new Person(this.email);
  }
}
