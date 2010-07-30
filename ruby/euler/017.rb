SINGLES = ['zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine', 'ten']
TEN_TWENTY = ['ten', 'eleven', 'twelve', 'thirteen', 'fourteen', 'fifteen', 'sixteen', 'seventeen', 'eighteen', 'nineteen', 'twenty']
TENS = ['zero', 'ten', 'twenty', 'thirty', 'forty', 'fifty', 'sixty', 'seventy', 'eighty', 'ninety']
HUNDRED = 'hundred'

class Integer
  def english_name
    raise "Cannot handle number larger than 1000" if self > 1000

    return SINGLES[self] if self < 10
    return TEN_TWENTY[self - 10] if self < 20
    return "one thousand" if self == 1000

    hundred, tens = self.divmod 100
    r = ""
    r << "#{hundred.english_name} #{HUNDRED}" if hundred > 0
    if tens > 0
      r << " and" if hundred > 0
      if tens > 10 and tens < 20
        r << " " << TEN_TWENTY[tens - 10]
      else
        tens, ones = tens.divmod 10
        r << " " << TENS[tens] if tens > 0
        r << " " << SINGLES[ones] if ones > 0
      end
    end
    r
  end
end

r = 0
(1..1000).each do |i|
  r += i.english_name.gsub(' ', '').size
end

p r

raise "Error" unless r == 21124