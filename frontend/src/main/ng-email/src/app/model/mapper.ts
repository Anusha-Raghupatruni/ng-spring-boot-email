export interface Mapper<T> {

  /**
   * Proves a way to map any kind of data to the specified type
   * @param input can be any type
   * @return {T}
   */
  map(input: any): T;
}
